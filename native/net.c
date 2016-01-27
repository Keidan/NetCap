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
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <linux/if_ether.h>
#include <linux/if_packet.h>
#include <time.h>
#include <inttypes.h>

/**
 * @typedef pcap_hdr_t
 * @brief Global header
 * Source: http://wiki.wireshark.org/Development/LibpcapFileFormat
 */
typedef struct pcap_hdr_s {
    unsigned int   magic_number;  /**< magic number */
    unsigned short version_major; /**< major version number */
    unsigned short version_minor; /**< minor version number */
    int            thiszone;      /**< GMT to local correction */
    unsigned int   sigfigs;       /**< accuracy of timestamps */
    unsigned int   snaplen;       /**< max length of captured packets, in octets */
    unsigned int   network;       /**< data link type */
} pcap_hdr_t;


/**
 * @typedef pcaprec_hdr_t
 * @brief Packet header
 * Source: http://wiki.wireshark.org/Development/LibpcapFileFormat
 */
typedef struct pcaprec_hdr_s {
    unsigned int   ts_sec;        /**< timestamp seconds */
    unsigned int   ts_usec;       /**< timestamp microseconds */
    unsigned int   incl_len;      /**< number of octets of packet saved in file */
    unsigned int   orig_len;      /**< actual length of packet */
} pcaprec_hdr_t;

/**
 * @def PCAP_VERSION_MAJOR
 * @brief Major version of the PCAP file.
 * @see net_pcap_global_hdr
 */
#define PCAP_VERSION_MAJOR     2
/**
 * @def PCAP_VERSION_MINOR
 * @brief Minor version of the PCAP file.
 * @see net_pcap_global_hdr
 */
#define PCAP_VERSION_MINOR     4
/**
 * @def PCAP_MAGIC_NATIVE
 * @brief PCAP Magic.
 * @see net_pcap_global_hdr
 */
#define PCAP_MAGIC_NATIVE      0xa1b2c3d4
/**
 * @def PCAP_MAGIC_SWAPPED
 * @brief PCAP Magic.
 * @see net_pcap_global_hdr
 */
#define PCAP_MAGIC_SWAPPED     0xd4c3b2a1
/**
 * @def PCAP_LINKTYPE_ETHERNET
 * @brief Capture type.
 * @see net_pcap_global_hdr
 */
#define PCAP_LINKTYPE_ETHERNET 1
/**
 * @def PCAP_SNAPLEN
 * @brief Capture size.
 * @see net_pcap_global_hdr
 */
#define PCAP_SNAPLEN           65535

#ifndef ETHER_TYPE
#define ETHER_TYPE             0x0800
#endif

char netcap_error[NETCAP_ERROR_LEN];


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
static void netcap_write_packet(FILE* output, unsigned int link, const unsigned char* buffer, size_t a_length, size_t r_length, _Bool *first);


/**
 * Open The capture socket to the iface and bind-it
 * @param iface The iface name.
 * @param promisc Enable or not the promiscuous mode.
 * @return the FD else -1 on error (see netcap_error).
 */
int netcap_open(const char* iface, _Bool promisc) {
  struct sockaddr_ll sll;
  int sockopt;
  struct ifreq ifr;	/* set promiscuous mode */
  unsigned int index = if_nametoindex(iface);
  if(!index) {
    sprintf(netcap_error, "The interface '%s' was not found.", iface);
    return -1;
  }
  int fd = socket(PF_PACKET, SOCK_RAW, htons(ETHER_TYPE));
  if(fd < 0) {
    sprintf(netcap_error, "Unable to open the raw socket for the iface '%s': (%d) %s", iface, errno, strerror(errno));
    return -1;
  }
  memset(&sll, 0, sizeof(sll));

  /* Allow the socket to be reused - incase connection is closed prematurely */
  if (setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &sockopt, sizeof sockopt) == -1) {
    sprintf(netcap_error, "Unable to set socket reuse: (%d) %s", errno, strerror(errno));
    close(fd);
    return -1;
  }
  /* Bind to device */
  if (setsockopt(fd, SOL_SOCKET, SO_BINDTODEVICE, iface, IFNAMSIZ-1) == -1)	{
    sprintf(netcap_error, "Unable to bind to the iface '%s': (%d) %s", iface, errno, strerror(errno));
    close(fd);
    return -1;
  }

  /* Set interface to promiscuous mode - do we need to do this every time? */
  strncpy(ifr.ifr_name, iface, IFNAMSIZ-1);
  if(promisc) {
    if(ioctl(fd, SIOCGIFFLAGS, &ifr) == -1) {
      sprintf(netcap_error, "Unable to retrieve the flags list for the iface '%s': (%d) %s", iface, errno, strerror(errno));
      close(fd);
      return -1;
    }
    ifr.ifr_flags |= IFF_PROMISC;
    if(ioctl(fd, SIOCSIFFLAGS, &ifr) == -1) {
      sprintf(netcap_error, "Unable to add the promiscuous flag for the iface '%s': (%d) %s", iface, errno, strerror(errno));
      close(fd);
      return -1;
    }
  } else {
    if(ioctl(fd, SIOCGIFFLAGS, &ifr) == -1) {
      sprintf(netcap_error, "Unable to retrieve the flags list for the iface '%s': (%d) %s", iface, errno, strerror(errno));
      close(fd);
      return -1;
    }
    ifr.ifr_flags &= ~IFF_PROMISC;
    if(ioctl(fd, SIOCSIFFLAGS, &ifr) == -1) {
      sprintf(netcap_error, "Unable to remove the promiscuous flag for the iface '%s': (%d) %s", iface, errno, strerror(errno));
      close(fd);
      return -1;
    }
  }

  return fd;
}

/**
 * Capture the interfaces packets.
 * @param net_list FD list.
 * @param length FD list length.
 * @param capfile The output capture file.
 * @param end Terminate the process.
 * @param display Display the captured packet number.
 * @return -1 on error.
 */
int netcap_process(net_list_t net_list, size_t length, FILE* capfile, _Bool *end, _Bool display) {
  int i, max_fd, fd;
  fd_set rset;
  unsigned char* buffer = NULL;
  _Bool first = true;
  do {
    FD_ZERO(&rset);
    max_fd = 0;
    //add child sockets to set
    for (i = 0; i < length; i++) {
      //socket descriptor
      fd = net_list[i].fd;
      //if valid socket descriptor then add to read list
      if(fd > 0) FD_SET(fd ,&rset);
      //highest file descriptor number, need it for the select function
      if(fd > max_fd) max_fd = fd;
    }
    if(!max_fd) break;
    //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
    int activity = select(max_fd + 1, &rset, NULL, NULL, NULL);
    if ((activity < 0) && (errno!=EINTR)) {
      sprintf(netcap_error, "select failed: (%d) %s", errno, strerror(errno));
      return -1;
    }
    //If something happened on the server socket , then its an incoming connection
    for (i = 0; i < length; i++) {
      //socket descriptor
      fd = net_list[i].fd;
      if (FD_ISSET(fd, &rset)) {

        /* Get the available datas to read */
        unsigned int available;
        int ret = ioctl(fd, FIONREAD, &available);
        if (ret == -1) {
          sprintf(netcap_error, "Unable to get the available datas to read for the iface '%s': (%d) %s.", net_list[i].name, errno, strerror(errno));
          return -1;
        }
        /* The size is valid ? */
        if(!available) {
          sprintf(netcap_error, "Zero length for the iface '%s'", net_list[i].name);
          break;
        }
        /* buffer alloc */
        buffer = malloc(available);
        if(!buffer) {
          /* A failure at this point is very critical. */
	  sprintf(netcap_error, "Not enough memory to allocate the packet buffer for the iface '%s'.", net_list[i].name);
	  return -1;
        }
        /* Reads the packet */
        ret = recvfrom(fd, buffer, available, 0, NULL, NULL);
        /* If the read fails, we go to the next packet */
        if (ret < 0) {
	  free(buffer);
	  sprintf(netcap_error, "recvfrom failed for the iface '%s': (%d) %s.", net_list[i].name, errno, strerror(errno));
	  break;
        }
	netcap_write_packet(capfile, PCAP_LINKTYPE_ETHERNET, buffer, available, ret, &first);
	net_list[i].count++;
	if(display)
	  fprintf(stderr, "Captured packet %"PRIu64" with length %zu for iface %s\n", net_list[i].count, (size_t)ret, net_list[i].name);
        free(buffer);
        break;
      }
    }
  }while((*end) == false);
  return 0;
}

/**
 * @brief Build the main header of the pcap file.
 * @param link Data link type.
 * @return pcap_hdr_t
 */
static pcap_hdr_t netcap_global_hdr(unsigned int link) {
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
static pcaprec_hdr_t netcap_packet_hdr(unsigned int incl_len, unsigned int ori_len) {
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
static void netcap_write_packet(FILE* output, unsigned int link, const unsigned char* buffer, size_t a_length, size_t r_length, _Bool *first) {
  if(*first) {
    pcap_hdr_t ghdr = netcap_global_hdr(link);
    fwrite(&ghdr, 1, sizeof(pcap_hdr_t), output);
    *first = 0;
  }
  pcaprec_hdr_t phdr = netcap_packet_hdr(r_length, a_length);
  fwrite(&phdr, 1, sizeof(pcaprec_hdr_t), output);
  fwrite(buffer, 1, r_length, output);
  fflush(output);
}

