/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
#ifndef __NET_IFACE_HPP__
#define __NET_IFACE_HPP__

#include <iostream>
#include <string>

class NetIFace
{
public:
  /**
   * Constructor
   * @param[in] name The iface name.
   * @param[in] promisc Enable or not the promiscuous mode.
   */
  NetIFace(std::string &name, bool promisc);

  virtual ~NetIFace() = default;

  /**
   * Open The capture socket to the iface and bind-it
   * @return false on error (see getError).
   */
  auto open() -> bool;

  /**
   * @brief Closes socket.
   */
  auto close() -> void;

  /**
   * @brief Returns the interface name.
   * @return std::string&
   */
  auto getName() -> std::string&;

  /**
   * @brief Returns the current FD.
   * @return int
   */
  auto getFD() const -> int;

  /**
   * Returns the packet counter.
   * @return std::uint64_t&
   */
  auto getCount() -> std::uint64_t&;

  /**
   * @brief Returns the last error.
   * @return std::string&
   */
  auto getError() -> std::string&;

private:
  std::string &m_name;
  bool m_promisc;
  int m_fd;
  std::uint64_t m_count;
  std::string m_error;
};
#endif /* __NET_IFACE_HPP__ */
