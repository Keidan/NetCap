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

  #include <arpa/inet.h>
  #include <linux/tcp.h>
  #include <linux/udp.h>
  #include <linux/ip.h>
  #include <linux/ipv6.h>
  #include <linux/if_ether.h>
  #include <linux/if_arp.h>

  struct arphdr2 {
    unsigned char sha[ETH_ALEN];
    unsigned char sip[4];
    unsigned char tha[ETH_ALEN];
    unsigned char tip[4];
  };

  #define IP_RF 0x8000        /* reserved fragment flag */
  #define IP_DF 0x4000        /* dont fragment flag */
  #define IP_MF 0x2000        /* more fragments flag */
  #define IP_OFFMASK 0x1fff   /* mask for fragmenting bits */

  //DNS header structure
  struct dns_header_s {
      unsigned short                 id;         // identification number
      unsigned char                  qr:1;       // query/response flag
      unsigned char                  opcode:4;   // purpose of message
      unsigned char                  aa:1;       // authoritive answer
      unsigned char                  tc:1;       // truncated message
      unsigned char                  rd:1;       // recursion desired
      unsigned char                  ra:1;       // recursion availables
      unsigned char                  zero:3;       // zero
      unsigned char                  rcode:4;    // response code
      unsigned short                 q_count;    // number of question entries
      unsigned short                 ans_count;  // number of answer entries
      unsigned short                 auth_count; // number of authority entries
      unsigned short                 add_count;  // number of resource entries
  };
  //DNS entry
  struct dns_query_entry_s {
      unsigned short                 type;
      unsigned short                 clazz;
  };
  struct dns_entry_s {
      unsigned short                 name;
      unsigned short                 type;
      unsigned short                 clazz;
      union {
    	  unsigned int               ttl;
    	  unsigned short             ttls[2];
      } u;
      unsigned short                 data_length;
      unsigned int                   addr;
  };

  /*
   * IGMPv1/v2 query and host report format.
   */
  struct igmphdr {
	  unsigned char		type;	/* version & type of IGMP message  */
	  unsigned char		max_resp_time;	/* subtype for routing msgs        */
	  unsigned short	cksum;	/* IP-style checksum               */
	  struct in_addr	group;	/* group address being reported    */
  };							/*  (zero for queries)             */
  /*
   * IGMP v3 query format.
   */
  struct igmp3hdr_query {
	  unsigned char		type;	/* version & type of IGMP message  */
	  unsigned char		max_resp_time;	/* subtype for routing msgs        */
	  unsigned short	cksum;	/* IP-style checksum               */
	  struct in_addr	group;	/* group address being reported    */
  								/*  (zero for queries)             */

	  unsigned char     resv:4;
	  unsigned char     s:1;
	  unsigned char     qrv:3;
	  unsigned char     qqic;
	  unsigned short	numsrc;	/* number of sources               */
  	/*struct in_addr	sources[1];*/ /* source addresses */
  };
  /*
   * IGMP v3 report format.
   */
  struct igmp3hdr_report {
	  unsigned char		type;
	  unsigned char		reserved0;
	  unsigned short	cksum;
	  unsigned short	reserved1;
	  unsigned short	numgrp;
      /*struct in_addr	sources[1];*/ /* source addresses */
  };

  #define NET_IP4_LEN 16
  #define NET_MAC_LEN 18

  struct net_iface_s {
      char name[32];
      char ip4[NET_IP4_LEN];
      char bcast[NET_IP4_LEN];
      char mask[NET_IP4_LEN];
      char mac[NET_MAC_LEN];
      int family;
      int metric;
      int mtu;
      short int flags;
      int index;
      struct net_iface_s *next;
  };

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

  #define pcap_magic_str(magic) (magic == PCAP_MAGIC_NATIVE ? "NATIVE" : ( magic == PCAP_MAGIC_SWAPPED ? "SWAPPED" : "UNKNOWN"))

  /**
   * List all network interfaces (only the name is filled).
   * @param devices The devices list.
   * @return -1 on error.
   */
  int net_list_ifaces(struct net_iface_s **devices);

  /**
   * Release the resources allocated by the list.
   * @param devices The list to release.
   */
  void net_release_ifaces(struct net_iface_s *devices);

  /**
   * Read the iface.
   * @param device The iface to read.
   * @return -1 on error.
   */
  int net_read_iface(struct net_iface_s *device);

  /**
   * Test if the file is in PCAP format.
   * @param filename The pcap file name.
   * @return -1 on error, 0 false, 1 true.
   */
  int net_is_pcap(const char* filename);

  /**
   * Read PCAP header.
   * @param filename The pcap file name.
   * @param ghdr The pcap header.
   * @return -1 on error, 0 else.
   */
  int net_read_pcap_header(const char* filename, pcap_hdr_t *ghdr);

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

  /**
   * Normalize the DNS name.
   * @param name The buffer is changed after a call.
   */
  void net_dns_normalize_name(unsigned char* name);

#endif /* __NET_H__ */
