#ifndef __NET_H__
  #define __NET_H__

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

#endif /* __NET_H__ */
