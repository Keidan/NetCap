/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
#include "Net.hpp"
#include <net/if.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <linux/if_packet.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <cerrno>
#include <unistd.h>
#include <cstring>
#include <algorithm>


#ifndef ETHER_TYPE
#define ETHER_TYPE             0x0800
#endif

/**
 * Constructor
 * @param[in] name The iface name.
 * @param[in] promisc Enable or not the promiscuous mode.
 */
NetIFace::NetIFace(std::string &name, bool promisc) : m_name(name),
                                                      m_promisc(promisc), m_fd(-1),
                                                      m_count(0), m_error("")
{
}

/**
 * Open The capture socket to the iface and bind-it
 * @return false on error (see getError).
 */
auto NetIFace::open() -> bool
{
  struct sockaddr_ll sll{};
  int sockopt;
  struct ifreq ifr{};	/* set promiscuous mode */
  auto index = if_nametoindex(m_name.c_str());
  if(!index)
  {
    m_error = "The interface '";
    m_error += m_name;
    m_error += "' was not found.";
    return false;
  }
  m_fd = socket(PF_PACKET, SOCK_RAW, htons(ETHER_TYPE));
  if(m_fd < 0)
  {
    m_error = "Unable to open the raw socket for the iface '";
    m_error += m_name;
    m_error += "': (" + std::to_string(errno) + ") ";
    m_error += strerror(errno);
    return false;
  }
  memset(&sll, 0, sizeof(sll));

  /* Allow the socket to be reused - incase connection is closed prematurely */
  if (setsockopt(m_fd, SOL_SOCKET, SO_REUSEADDR, &sockopt, sizeof sockopt) == -1)
  {
    m_error = "Unable to set socket reuse for the iface '";
    m_error += m_name;
    m_error += "': (" + std::to_string(errno) + ") ";
    m_error += strerror(errno);
    close();
    return false;
  }
  /* Bind to device */
  if (setsockopt(m_fd, SOL_SOCKET, SO_BINDTODEVICE, m_name.c_str(), IFNAMSIZ-1) == -1)
  {
    m_error = "Unable to bind to the iface '";
    m_error += m_name;
    m_error += "': (" + std::to_string(errno) + ") ";
    m_error += strerror(errno);
    close();
    return false;
  }

  /* Set interface to promiscuous mode - do we need to do this every time? */
  strncpy(ifr.ifr_name, m_name.c_str(), IFNAMSIZ-1);
  if(m_promisc)
  {
    if(ioctl(m_fd, SIOCGIFFLAGS, &ifr) == -1)
    {
      m_error = "Unable to retrieve the flags list for the iface '";
      m_error += m_name;
      m_error += "': (" + std::to_string(errno) + ") ";
      m_error += strerror(errno);
      close();
      return false;
    }
    ifr.ifr_flags |= IFF_PROMISC;
    if(ioctl(m_fd, SIOCSIFFLAGS, &ifr) == -1)
    {
      m_error = "Unable to add the promiscuous flag for the iface '";
      m_error += m_name;
      m_error += "': (" + std::to_string(errno) + ") ";
      m_error += strerror(errno);
      close();
      return false;
    }
  } else {
    if(ioctl(m_fd, SIOCGIFFLAGS, &ifr) == -1)
    {
      m_error = "Unable to retrieve the flags list for the iface '";
      m_error += m_name;
      m_error += "': (" + std::to_string(errno) + ") ";
      m_error += strerror(errno);
      close();
      return -1;
    }
    ifr.ifr_flags &= ~IFF_PROMISC;
    if(ioctl(m_fd, SIOCSIFFLAGS, &ifr) == -1)
    {
      m_error = "Unable to remove the promiscuous flag for the iface '";
      m_error += m_name;
      m_error += "': (" + std::to_string(errno) + ") ";
      m_error += strerror(errno);
      close();
      return false;
    }
  }
  return true;
}

/**
 * @brief Closes socket.
 */
auto NetIFace::close() -> void
{
  if(m_fd != -1)
  {
    ::close(m_fd);
    m_fd = -1;
  }
}

/**
 * @brief Returns the interface name.
 * @return std::string&
 */
auto NetIFace::getName() -> std::string&
{
  return m_name;
}

/**
 * @brief Returns the current FD.
 * @return int
 */
auto NetIFace::getFD() const -> int
{
  return m_fd;
}

/**
 * Returns the packet counter.
 * @return std::uint64_t&
 */
auto NetIFace::getCount() -> std::uint64_t&
{
  return m_count;
}

/**
 * @brief Returns the last error.
 * @return std::string&
 */
auto NetIFace::getError() -> std::string&
{
  return m_error;
}
