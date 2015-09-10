#include "net.h"
#include <net/if.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <strings.h>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>

char n_errno[NET_ERRNO_LEN];

#ifdef __ANDROID__
#include <android/log.h>
#define LOGGER_FCT(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, "NetworkInterface", fmt, ##__VA_ARGS__)
#else
#define LOGGER_FCT(fmt, ...) fprintf(stderr, "NetworkInterface ->"fmt, ##__VA_ARGS__)
#endif /* __ANDROID__ */

/**
 * List all network interfaces (only the name is filled).
 * @param devices The devices list.
 * @return -1 on error.
 */
int net_list_ifaces(struct net_iface_s **devices) {
  char buf[1024];
  int n_ifaces, i, fd;
  struct ifreq *iff;
  struct ifconf ifc;
  struct net_iface_s *device;
  bzero(n_errno, NET_ERRNO_LEN);

  /* Get a socket handle. */
  fd = socket(AF_INET, SOCK_DGRAM, 0);
  if(fd < 0) {
    sprintf(n_errno, "socket: (%d) %s.", errno, strerror(errno));
    return -1;
  }
  /* Query available interfaces. */
  ifc.ifc_len = sizeof(buf);
  ifc.ifc_buf = buf;
  if(ioctl(fd, SIOCGIFCONF, &ifc) < 0) {
    sprintf(n_errno, "SIOCGIFCONF: (%d) %s.", errno, strerror(errno));
    close(fd);
    return -1;
  }
  close(fd);

  /* Iterate through the list of interfaces. */
  iff = ifc.ifc_req;
  n_ifaces = ifc.ifc_len / sizeof(struct ifreq);
  for(i = 0; i < n_ifaces; i++) {
    struct ifreq *item = &iff[i];
    device = malloc(sizeof(struct net_iface_s));
    if(!device) {	
      sprintf(n_errno, "Unable to allocate the memory");
      return -1;
    }
    bzero(device, sizeof(struct net_iface_s));
    strcpy(device->name, item->ifr_name);
    if(!(*devices))
      (*devices) = device;
    else{
      device->next = (*devices)->next;
      (*devices)->next = device;
    }
    
  }
  return 0;
}

/**
 * Release the resources allocated by the list.
 * @param devices The list to release.
 */
void net_release_ifaces(struct net_iface_s *devices) {
  struct net_iface_s *node, *tdevices = devices;
  while(tdevices) {
    node = tdevices;
    tdevices = tdevices->next;
    free(node);
  }
}


/**
 * Read the iface.
 * @param device The iface to read.
 * @return -1 on error.
 */
int net_read_iface(struct net_iface_s *device) {
  struct ifreq devea;
  struct sockaddr_in *sa;

  int fd = socket(AF_INET, SOCK_DGRAM, 0);
  if(fd == -1) {
    sprintf(n_errno, "Unable to open the socket: (%d) %s", errno, strerror(errno));
    return -1;
  }

  strcpy(devea.ifr_name, device->name);
  // Get the flags list
  if (ioctl(fd, SIOCGIFFLAGS, &devea) == 0)
    device->flags = devea.ifr_flags;
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the iface flags: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */
  // Get the iface index
  if (ioctl(fd, SIOCGIFINDEX, &devea) == 0)
    device->index = devea.ifr_ifindex;
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the iface index: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */
  
  // Get the IPv4 address
  if(ioctl(fd, SIOCGIFADDR, &devea) == 0) {
    sa = (struct sockaddr_in *)&devea.ifr_addr;
    strncpy(device->ip4, inet_ntoa(sa->sin_addr), NET_IP4_LEN);
  }
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the ipv4 address: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */


  // Get the sub netmask address
  if (ioctl(fd, SIOCGIFNETMASK, &devea) == 0) {
    sa = (struct sockaddr_in*) &devea.ifr_netmask;
    strncpy(device->mask, inet_ntoa(sa->sin_addr), NET_IP4_LEN);
  }
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the netmask address: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */

  // Get the broadcast address
  if (ioctl(fd, SIOCGIFBRDADDR, &devea) == 0) {
    struct sockaddr_in *sbcast = (struct sockaddr_in *)&devea.ifr_broadaddr;
    strncpy(device->bcast, inet_ntoa(sbcast->sin_addr), NET_IP4_LEN);
  }
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the broad cast address: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */

  // Get the mac address and familly
  if (ioctl(fd, SIOCGIFHWADDR, &devea) == 0) {
    device->family = devea.ifr_hwaddr.sa_family;
    snprintf(device->mac, NET_MAC_LEN, "%02x:%02x:%02x:%02x:%02x:%02x",
	    devea.ifr_hwaddr.sa_data[0]&0xFF,
	    devea.ifr_hwaddr.sa_data[1]&0xFF,
	    devea.ifr_hwaddr.sa_data[2]&0xFF,
	    devea.ifr_hwaddr.sa_data[3]&0xFF,
	    devea.ifr_hwaddr.sa_data[4]&0xFF,
	    devea.ifr_hwaddr.sa_data[5]&0xFF);
  } 
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the mac address: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */

  // Get the metric
  if (ioctl(fd, SIOCGIFMETRIC, &devea) == 0) {
    device->metric = devea.ifr_metric;
    if(!device->metric) device->metric++;
  }
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the iface metric: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */

  // Get the MTU
  if (ioctl(fd, SIOCGIFMTU, &devea) == 0)
    device->mtu = devea.ifr_mtu;
#ifdef LOGGER_FCT
  else
    LOGGER_FCT("Unable to get the iface mtu: (%d) %s", errno, strerror(errno));
#endif /* LOGGER_FCT */

  close(fd);
  return 0;
}
