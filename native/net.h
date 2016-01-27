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

  #include <stdio.h>
  #include <stdbool.h>
  #include <stdint.h>
  #include <string.h>
  #include <net/if.h>

  #define NETCAP_ERROR_LEN     1024
  extern char netcap_error[NETCAP_ERROR_LEN];

  #define MAX_FILE_DESCRIPTORS 1024
  struct net_list_s {
      char name[IFNAMSIZ];
      int fd;
      uint64_t count;
  };
  typedef struct net_list_s net_list_t[MAX_FILE_DESCRIPTORS];

  /**
   * Open The capture socket to the iface and bind-it
   * @param iface The iface name.
   * @param promisc Enable or not the promiscuous mode.
   * @return the FD else -1 on error (see netcap_error).
   */
  int netcap_open(const char* iface, _Bool promisc);

  /**
   * Capture the interfaces packets.
   * @param net_list FD list.
   * @param length FD list length.
   * @param capfile The output capture file.
   * @param end Terminate the process.
   * @param display Display the captured packet number.
   * @return -1 on error.
   */
  int netcap_process(net_list_t net_list, size_t length, FILE* capfile, _Bool *end, _Bool display);
#endif /* __NET_H__ */
