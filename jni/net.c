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

