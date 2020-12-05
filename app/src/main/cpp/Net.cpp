/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
#include "Net.hpp"
#include <net/if.h>
#include <cerrno>
#include <sys/ioctl.h>
#include <ctime>
#include <cstring>

Net::Net(std::ofstream &output, std::vector<NetIFace*> &netList) : m_output(output), m_netList(netList), m_error("")
{
}

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
auto Net::writePacket(std::uint32_t link, const std::uint8_t * buffer, std::size_t aLength, std::size_t rLength, bool *first) -> void
{
  if(*first)
  {
    PCAPHdr hdr;
    globalHdr(link, hdr);
    m_output.write((char*)&hdr, sizeof(PCAPHdr));
    *first = false;
  }
  PCAPRecHdr hdr;
  packetHdr(rLength, aLength, hdr);
  m_output.write((char*)&hdr, sizeof(PCAPRecHdr));
  m_output.write((char*)buffer, rLength);
  m_output.flush();
}

/**
 * @brief Build the main header of the pcap file.
 * @param[in] link Data link type.
 * @param[out] hdr PCAPHdr.
 */
auto Net::globalHdr(std::uint32_t link, PCAPHdr &hdr) -> void
{
  hdr.magic_number = PCAP_MAGIC_NATIVE;
  hdr.version_major = PCAP_VERSION_MAJOR;
  hdr.version_minor = PCAP_VERSION_MINOR;
  tzset(); /* reload the timezone field */
  hdr.thiszone = timezone;
  hdr.sigfigs = 0;
  hdr.snaplen = PCAP_SNAPLEN;
  hdr.network = link;
}

/**
 * @brief Build the packet header of the pcap file.
 * @param[in] inclLen Number of octets of packet saved in file.
 * @param[in] oriLen Actual length of packet.
 * @param[out] hdr PCAPRecHdr.
 */
auto Net::packetHdr(std::uint32_t inclLen, std::uint32_t oriLen, PCAPRecHdr &hdr) -> void
{
  timeval tv = { 0 };
  gettimeofday(&tv, nullptr);
  hdr.ts_sec = tv.tv_sec;
  hdr.ts_usec = tv.tv_usec;
  hdr.incl_len = inclLen;
  hdr.orig_len = oriLen;
}

/**
 * @brief Returns the last error.
 * @return std::string&
 */
auto Net::getError() -> std::string&
{
  return m_error;
}

/**
 * Capture the interfaces packets.
 * @param[in,out] capfile The output capture file.
 * @param[in] end Terminate the process.
 * @param[in] display Display the captured packet number.
 * @return -1 on error.
 */
auto Net::process(const bool *end, bool display) -> int
{
  int max_fd, fd;
  fd_set rset;
  auto first = true;
  do
  {
    FD_ZERO(&rset);
    max_fd = 0;
    //add child sockets to set
    for(auto iface : m_netList)
    {
      //socket descriptor
      fd = iface->getFD();
      //if valid socket descriptor then add to read list
      if(fd > 0)
      {
        FD_SET(fd ,&rset);
      }
      //highest file descriptor number, need it for the select function
      if(fd > max_fd)
      {
        max_fd = fd;
      }
    }
    if(max_fd == 0)
    {
      break;
    }

    //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
    auto activity = select(max_fd + 1, &rset, nullptr, nullptr, nullptr);
    if(*end)
    {
      break;
    }

    if ((activity < 0) && (errno!=EINTR))
    {
      m_error = "Select failed : (" + std::to_string(errno) + ") ";
      m_error += strerror(errno);
      return -1;
    }
    //If something happened on the server socket , then its an incoming connection
    for(auto iface : m_netList)
    {
      //socket descriptor
      fd = iface->getFD();
      if (FD_ISSET(fd, &rset))
      {
        /* Get the available datas to read */
        std::uint32_t available;
        auto ret = ioctl(fd, FIONREAD, &available);
        if (ret == -1)
        {
          m_error = "Unable to get the available datas to read for the iface '";
          m_error += iface->getName();
          m_error += "': (" + std::to_string(errno) + ") ";
          m_error += strerror(errno);
          return -1;
        }
        /* The size is valid ? */
        if(!available)
        {
          m_error = "Zero length for the iface '";
          m_error += iface->getName();
          m_error += "'.";
          break;
        }
        /* buffer alloc */
        auto buffer = new std::uint8_t[available];
        /* Reads the packet */
        ret = recvfrom(fd, buffer, available, 0, nullptr, nullptr);
        /* If the read fails, we go to the next packet */
        if (ret < 0)
        {
          delete [] buffer;
          m_error = "recvfrom failed for the iface '";
          m_error += iface->getName();
          m_error += "': (" + std::to_string(errno) + ") ";
          m_error += strerror(errno);
          break;
        }
        writePacket(PCAP_LINKTYPE_ETHERNET, buffer, available, ret, &first);
        iface->getCount()++;

        if(display)
        {
          std::cerr << "Captured packet " << std::to_string(iface->getCount()) << " with length " <<
                    std::to_string((std::size_t)ret) << " for iface " << iface->getName() << std::endl;
        }
        delete [] buffer;
        break;
      }
      if((*end))
      {
        break;
      }
    }
  }
  while(!(*end));
  return 0;
}
