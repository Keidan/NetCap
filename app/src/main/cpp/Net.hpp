/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
#ifndef __NET_HPP__
#define __NET_HPP__

#include "NetIFace.hpp"
#include <vector>
#include <fstream>

/**
 * @brief Global header
 * Source: http://wiki.wireshark.org/Development/LibpcapFileFormat
 */
typedef struct
{
  std::uint32_t magic_number;   /**< magic number */
  std::uint16_t version_major;  /**< major version number */
  std::uint16_t version_minor;  /**< minor version number */
  std::int32_t  thiszone;       /**< GMT to local correction */
  std::uint32_t sigfigs;        /**< accuracy of timestamps */
  std::uint32_t snaplen;        /**< max length of captured packets, in octets */
  std::uint32_t network;        /**< data link type */
} PCAPHdr;


/**
 * @brief Packet header
 * Source: http://wiki.wireshark.org/Development/LibpcapFileFormat
 */
typedef struct
{
  std::uint32_t ts_sec;         /**< timestamp seconds */
  std::uint32_t ts_usec;        /**< timestamp microseconds */
  std::uint32_t incl_len;       /**< number of octets of packet saved in file */
  std::uint32_t orig_len;       /**< actual length of packet */
} PCAPRecHdr;

/**
 * @brief Major version of the PCAP file.
 * @see net_pcap_global_hdr
 */
constexpr std::uint16_t PCAP_VERSION_MAJOR = 2;
/**
 * @brief Minor version of the PCAP file.
 * @see net_pcap_global_hdr
 */
constexpr std::uint16_t PCAP_VERSION_MINOR = 4;
/**
 * @brief PCAP Magic.
 * @see net_pcap_global_hdr
 */
constexpr std::uint32_t PCAP_MAGIC_NATIVE = 0xa1b2c3d4;
/**
 * @brief PCAP Magic.
 * @see net_pcap_global_hdr
 */
constexpr std::uint32_t PCAP_MAGIC_SWAPPED = 0xd4c3b2a1;
/**
 * @brief Capture type.
 * @see net_pcap_global_hdr
 */
constexpr std::uint32_t PCAP_LINKTYPE_ETHERNET = 1;
/**
 * @brief Capture size.
 * @see net_pcap_global_hdr
 */
constexpr std::uint32_t PCAP_SNAPLEN = 65535;


constexpr std::uint32_t MAX_FILE_DESCRIPTORS = 1024;

class Net
{
public:
  Net(std::ofstream &output, std::vector<NetIFace*> &netList);
  virtual ~Net() = default;


  /**
   * Capture the interfaces packets.
   * @param[in,out] capfile The output capture file.
   * @param[in] end Terminate the process.
   * @param[in] display Display the captured packet number.
   * @return -1 on error.
   */
  auto process(const bool *end, bool display) -> int;

  /**
   * @brief Returns the last error.
   * @return std::string&
   */
  auto getError() -> std::string&;


private:
  std::ofstream &m_output;
  std::vector<NetIFace*> &m_netList;
  std::string m_error;

  /**
   * @brief Writes all pcap headers and the packet buffer into the specified file.
   * Source: http://wiki.wireshark.org/Development/LibpcapFileFormat
   * Packet structure:
   * -----------------------------------------------------------------------------------------------------------------
   * | Global Header | Packet Header | Packet Data | Packet Header | Packet Data | Packet Header | Packet Data | ... |
   * -----------------------------------------------------------------------------------------------------------------
   * @param[in] link Data link type.
   * @param[in] buffer Input buffer .
   * @param[in] aLength Size before the call of the recvfrom function.
   * @param[in] rLength Size after the call of the recvfrom function.
   * @param[in,out] first Memorize if we need to write the first packet header.
   */
  auto writePacket(std::uint32_t link, const std::uint8_t * buffer, std::size_t aLength, std::size_t rLength, bool *first) -> void;

  /**
   * @brief Build the main header of the pcap file.
   * @param[in] link Data link type.
   * @param[out] hdr PCAPHdr.
   */
  static auto globalHdr(std::uint32_t link, PCAPHdr &hdr) -> void;

  /**
   * @brief Build the packet header of the pcap file.
   * @param[in] inclLen Number of octets of packet saved in file.
   * @param[in] oriLen Actual length of packet.
   * @param[out] hdr PCAPRecHdr.
   */
  static auto packetHdr(std::uint32_t inclLen, std::uint32_t oriLen, PCAPRecHdr &hdr) -> void;
};

#endif /* __NET_HPP__ */
