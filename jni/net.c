/**
 *******************************************************************************
 * @file net.c
 * @author Keidan
 * @date 07/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright
 * Copyright 2015 Keidan, all right reserved
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY.
 *
 * License summary :
 *    You can modify and redistribute the sources code and binaries.
 *    You can send me the bug-fix
 *
 * Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */

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
#include <linux/if_ether.h>
#include <linux/if_packet.h>
#include <time.h>

#define ETHER_TYPE	0x0800

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

/**
 * Read PCAP header.
 * @param filename The pcap file name.
 * @param ghdr The pcap header.
 * @return -1 on error, 0 else.
 */
int net_read_pcap_header(const char* filename, pcap_hdr_t *ghdr) {
  FILE *file = fopen(filename, "r");
  if(!file) {
    sprintf(n_errno, "Unable to open the file '%s': (%d) %s.", filename, errno, strerror(errno));
    return -1;
  }
  unsigned int reads = fread(ghdr, 1, sizeof(pcap_hdr_t), file);
  fclose(file);
  if(reads != sizeof(pcap_hdr_t)) {
    sprintf(n_errno, "The file '%s' has an invalid header size.", filename);
    return -1;
  }
  return 0;
}

/**
 * Test if the file is in PCAP format.
 * @param filename The pcap file name.
 * @return -1 on error, 0 false, 1 true.
 */
int net_is_pcap(const char* filename) {
  pcap_hdr_t ghdr;
  int ret = net_read_pcap_header(filename, &ghdr);
  if(ret != -1) {
    if(ghdr.magic_number != PCAP_MAGIC_NATIVE && ghdr.magic_number != PCAP_MAGIC_SWAPPED)
      ret = 0;
    else ret = 1;
  }
  return ret;
}

/**
 * Open The capture socket to the iface and bind-it
 * @param iface The iface name.
 * @return the FD else -1 on error.
 */
int net_capture_open(const char* iface, int promisc) {
  struct sockaddr_ll sll;
  int sockopt;
  struct ifreq ifr;	/* set promiscuous mode */
  unsigned int index = if_nametoindex(iface);
  if(!index) {
    sprintf(n_errno, "No interface found.");
    return -1;
  }
  int fd = socket(PF_PACKET, SOCK_RAW, htons(ETHER_TYPE));
  if(fd < 0) {
    sprintf(n_errno, "socket: (%d) %s", errno, strerror(errno));
    return -1;
  }
  memset(&sll, 0, sizeof(sll));

  /* Set interface to promiscuous mode - do we need to do this every time? */
  strncpy(ifr.ifr_name, iface, IFNAMSIZ-1);
  if(promisc) {
    ioctl(fd, SIOCGIFFLAGS, &ifr);
    ifr.ifr_flags |= IFF_PROMISC;
    ioctl(fd, SIOCSIFFLAGS, &ifr);
  }
  /* Allow the socket to be reused - incase connection is closed prematurely */
  if (setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &sockopt, sizeof sockopt) == -1) {
    sprintf(n_errno, "setsockopt: (%d) %s", errno, strerror(errno));
    close(fd);
    return -1;
  }
  /* Bind to device */
  if (setsockopt(fd, SOL_SOCKET, SO_BINDTODEVICE, iface, IFNAMSIZ-1) == -1)	{
    sprintf(n_errno, "SO_BINDTODEVICE: (%d) %s", errno, strerror(errno));
    close(fd);
    return -1;
  }
  return fd;
}

/**
 * Capture the interfaces packets.
 * @param fds FD list.
 * @param len FD list length.
 * @param ptr User pointer.
 * @param cb callback.
 * @return -1 on error.
 */
int net_capture_process(int *fds, int len, void* ptr, void(*cb)(void* ptr, unsigned char* buffer, unsigned int length)) {
  int i, max_fd, fd;
  fd_set rset;
  unsigned char* buffer = NULL;
  do {
    FD_ZERO(&rset);
    max_fd = 0;
    //add child sockets to set
    for (i = 0; i < len; i++) {
      //socket descriptor
      fd = fds[i];
      //if valid socket descriptor then add to read list
      if(fd > 0) FD_SET(fd ,&rset);
      //highest file descriptor number, need it for the select function
      if(fd > max_fd) max_fd = fd;
    }
    if(!max_fd) break;
    //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
    int activity = select(max_fd + 1, &rset, NULL, NULL, NULL);
    if ((activity < 0) && (errno!=EINTR)) {
      sprintf(n_errno, "select failed: (%d) %s", errno, strerror(errno));
      return -1;
    }
    //If something happened on the server socket , then its an incoming connection
    for (i = 0; i < len; i++) {
      //socket descriptor
      fd = fds[i];
      if (FD_ISSET(fd, &rset)) {

        /* Get the available datas to read */
        unsigned int available;
        int ret = ioctl(fd, FIONREAD, &available);
        if (ret == -1) {
          sprintf(n_errno, "available: (%d) %s.", errno, strerror(errno));
          return -1;
        }
        /* The size is valid ? */
        if(!available) {
          sprintf(n_errno, "Zero length o_O ?");
          break;
        }
        /* buffer alloc */
        buffer = malloc(available);
        if(!buffer) {
          /* A failure at this point is very critical. */
  	      sprintf(n_errno, "Malloc failed!");
  	      return -1;
        }
        /* Reads the packet */
        ret = recvfrom(fd, buffer, available, 0, NULL, NULL);
        /* If the read fails, we go to the next packet */
        if (ret < 0) {
  	      free(buffer);
  	      sprintf(n_errno, "recvfrom failed: (%d) %s.\n", errno, strerror(errno));
  	      break;
        }
        if(cb) cb(ptr, buffer, ret);
        free(buffer);
        break;
      }
    }
  }while(1);
  return 0;
}

/**
 * @brief Build the main header of the pcap file.
 * @param link Data link type.
 * @return pcap_hdr_t
 */
pcap_hdr_t net_pcap_global_hdr(unsigned int link) {
  pcap_hdr_t hdr;
  memset(&hdr, 0, sizeof(pcap_hdr_t));
  hdr.magic_number = PCAP_MAGIC_NATIVE;
  hdr.version_major = PCAP_VERSION_MAJOR;
  hdr.version_minor = PCAP_VERSION_MINOR;
  tzset(); /* reload the timezone field */
  hdr.thiszone = timezone;
  hdr.sigfigs = 0;
  hdr.snaplen = PCAP_SNAPLEN;
  hdr.network = link;
  return hdr;
}
/**
 * @brief Build the packet header of the pcap file.
 * @return pcaprec_hdr_t.
 */
pcaprec_hdr_t net_pcap_packet_hdr(unsigned int incl_len, unsigned int ori_len) {
  pcaprec_hdr_t hdr;
  struct timeval tv;
  gettimeofday(&tv, NULL);
  hdr.ts_sec = tv.tv_sec;
  hdr.ts_usec = tv.tv_usec;
  hdr.incl_len = incl_len;
  hdr.orig_len = ori_len;
  return hdr;
}

/**
 * @brief Writes all pcap headers and the packet buffer into the specified file.
 * Source: http://wiki.wireshark.org/Development/LibpcapFileFormat
 * Packet structure:
 * -----------------------------------------------------------------------------------------------------------------
 * | Global Header | Packet Header | Packet Data | Packet Header | Packet Data | Packet Header | Packet Data | ... |
 * -----------------------------------------------------------------------------------------------------------------
 * @param output Output file
 * @param link Data link type.
 * @param buffer Input buffer .
 * @param a_length Size before the call of the recvfrom function.
 * @param r_length Size after the call of the recvfrom function.
 * @param first Memorize if we need to write the first packet header.
 */
void net_pcap_write_packet(FILE* output, unsigned int link, const unsigned char* buffer, size_t a_length, size_t r_length, _Bool *first) {
  if(*first) {
    pcap_hdr_t ghdr = net_pcap_global_hdr(link);
    fwrite(&ghdr, 1, sizeof(pcap_hdr_t), output);
    *first = 0;
  }
  pcaprec_hdr_t phdr = net_pcap_packet_hdr(r_length, a_length);
  fwrite(&phdr, 1, sizeof(pcaprec_hdr_t), output);
  fwrite(buffer, 1, r_length, output);
  fflush(output);
}

