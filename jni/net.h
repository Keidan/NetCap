/**
 *******************************************************************************
 * @file net.h
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
#ifndef __NET_H__
  #define __NET_H__


  #define NET_ERRNO_LEN 1024
  extern char n_errno[NET_ERRNO_LEN];


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
  #define PCAP_MAGIC_SWAPPED      0xd4c3b2a1
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

  /**
   * Open The capture socket to the iface and bind-it
   * @param iface The iface name.
   * @return the FD else -1 on error.
   */
  int net_capture_open(const char* iface, int promisc);

  /**
   * Capture the interfaces packets.
   * @param fds FD list.
   * @param len FD list length.
   * @param ptr User pointer.
   * @param cb callback.
   * @return -1 on error.
   */
  int net_capture_process(int *fds, int len, void* ptr, void(*cb)(void* ptr, unsigned char* buffer, unsigned int length));

#endif /* __NET_H__ */
