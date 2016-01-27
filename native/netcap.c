/**
 *******************************************************************************
 * @file netcap.c
 * @author Keidan
 * @date 26/01/2016
 * @par Project
 * NetCap
 *
 * @par Copyright
 * Copyright 2016 Keidan, all right reserved
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
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <errno.h>
#include <linux/limits.h>
#include <getopt.h>
#include <unistd.h>
#include "net.h"

#define MAX_IFACES (IFNAMSIZ*MAX_FILE_DESCRIPTORS)

static const struct option long_options[] = { 
  { "help"        , 0, NULL, 'h' },
  { "iface"       , 1, NULL, 'i' },
  { "file"        , 1, NULL, 'f' },
  { "promiscuous" , 0, NULL, 'p' },
  { "display"     , 0, NULL, 'd' },
  { NULL          , 0, NULL,  0   },
};

static net_list_t net_list;
static size_t length = 0;
static _Bool end = false;
static FILE* capfile = NULL;

/**
 * @fn static void sig_catch(int sig)
 * @brief Trap A signal and leave.
 * @param sig the signal.
 */
static void sig_catch(int sig);
/**
 * @fn static void cleanup(void)
 * @brief Soft cleanup.
 */
static void cleanup(void);
/**
 * @fn static void usage(int xcode)
 * @brief usage function.
 * @param xcode The exit code.
 */
static void usage(int xcode);



int main(int argc, char** argv) {
  int opt, fd, i;
  _Bool promiscuous = false, display = false;
  char ifaces[MAX_IFACES];
  char file[PATH_MAX];
  char *token, *str_copy, *str_backup;
  struct sigaction sa;

  memset(&sa, 0, sizeof(struct sigaction));
  memset(ifaces, 0, MAX_IFACES);
  memset(file, 0, PATH_MAX);
  memset(net_list, 0, sizeof(net_list_t));

  /* exit + sigint registration */
  atexit(cleanup);
  sa.sa_handler = sig_catch;
  (void)sigaction(SIGINT, &sa, NULL);
  (void)sigaction(SIGTERM, &sa, NULL);

  /* parse the test options */
  while ((opt = getopt_long(argc, argv, "hi:f:pd", long_options, NULL)) != -1) {
    switch (opt) {
      case 'h': /* help */
        usage(EXIT_SUCCESS);
        break;
      case 'i':
	strncpy(ifaces, optarg, MAX_IFACES - 1);
	break;
      case 'f':
	strncpy(file, optarg, PATH_MAX - 1);
	break;
      case 'p':
	promiscuous = true;
	break;
      case 'd':
	display = true;
	break;
      default: /* '?' */
	fprintf(stderr, "Unsupported option\n");
        usage(EXIT_FAILURE);
    }
  }
  if(ifaces[0] == 0 || file[0] == 0)
    usage(EXIT_FAILURE);

  if(strstr(ifaces, ",") != NULL) {
    /* list de ifaces */
    str_copy = strdup(ifaces);
    str_backup = str_copy;
    length = 0;
    while ((token = strsep(&str_copy, ",")))
      strncpy(net_list[length++].name, token, IFNAMSIZ - 1);
    free(str_backup);
    for(i = 0; i < length; i++) {
      fd = netcap_open(net_list[i].name, promiscuous);
      if(fd == -1) {
	fprintf(stderr, "%s\n", netcap_error);
	exit(EXIT_FAILURE);
      }
      net_list[i].fd = fd;
    }
  } else {
    strncpy(net_list[0].name, ifaces, IFNAMSIZ - 1);
    fd = netcap_open(net_list[0].name, promiscuous);
    if(fd == -1) {
      fprintf(stderr, "%s\n", netcap_error);
      exit(EXIT_FAILURE);
    }
    length = 1;
    net_list[0].fd = fd;
  }

  FILE* capfile = fopen(file, "w+");
  if(capfile == NULL) {
    fprintf(stderr, "Unable to open the capture file: (%d) %s\n", errno, strerror(errno));
    exit(EXIT_FAILURE);
  }
  if(netcap_process(net_list, length, capfile, &end, display) == -1) {
    fprintf(stderr, "%s\n", netcap_error);
    exit(EXIT_FAILURE);
  }
  return 0;
}

/**
 * @fn static void usage(int xcode)
 * @brief usage function.
 * @param xcode The exit code.
 */
static void usage(int xcode) {
  fprintf(stderr, "usage: netcap [options]\n");
  fprintf(stderr, "\t--help, -h: Print this help\n");
  fprintf(stderr, "\t--iface, -i: The interface name or the interface list separated by the commas.\n");
  fprintf(stderr, "\t--file, -f: The output file name.\n");
  fprintf(stderr, "\t--promiscuous, -p: Switch the interface in the promiscuous mode.\n");
  fprintf(stderr, "\t--display, -d: Display the captured packet number.\n");
  exit(xcode);
}

/**
 * @fn static void sig_catch(int sig)
 * @brief Trap A signal and leave.
 * @param sig the signal.
 */
static void sig_catch(int sig) {
  exit(sig); /* call atexit */
}

/**
 * @fn static void cleanup(void)
 * @brief Soft cleanup.
 */
static void cleanup() {
  int i;
  end = true;
  if(capfile != NULL)
    fclose(capfile), capfile = NULL;
  for(i = 0; i < length; i++) {
    if(net_list[i].fd > 0)
      close(net_list[i].fd), net_list[i].fd = 0;
  }
}
