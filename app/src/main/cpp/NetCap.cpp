/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
#include <iostream>
#include <csignal>
#include <cerrno>
#include <climits>
#include <net/if.h>
#include <getopt.h>
#include <unistd.h>
#include <regex>
#include "Net.hpp"

static const struct option long_options[] =
    {
        { "help"        , 0, nullptr, 'h' },
        { "iface"       , 1, nullptr, 'i' },
        { "file"        , 1, nullptr, 'f' },
        { "promiscuous" , 0, nullptr, 'p' },
        { "display"     , 0, nullptr, 'd' },
        { nullptr       , 0, nullptr,  0  },
    };

static std::vector<NetIFace*> netList;
static bool end = false;
static std::ofstream capfile;


static auto sig_catch(int sig) -> void;
static auto cleanup() -> void;
static auto usage(int xcode) -> void;
auto split(const std::string& in, const std::string &reg) -> std::vector<std::string>;


auto main(int argc, char** argv) -> int
{
  int opt;
  bool promiscuous = false, display = false;
  std::string ifaces;
  std::string file;
  struct sigaction sa{};

  memset(&sa, 0, sizeof(struct sigaction));

  /* exit + sigint registration */
  atexit(cleanup);
  sa.sa_handler = sig_catch;
  sigaction(SIGINT, &sa, nullptr);
  sigaction(SIGTERM, &sa, nullptr);

  /* parse the test options */
  while ((opt = getopt_long(argc, argv, "hi:f:pd", long_options, nullptr)) != -1)
  {
    switch (opt)
    {
      case 'h': /* help */
        usage(EXIT_SUCCESS);
        break;
      case 'i':
        ifaces = optarg;
        break;
      case 'f':
        file = optarg;
        if(file.length() >= PATH_MAX - 1)
        {
          std::cerr << "The file parameter is too long" << std::endl;
          usage(EXIT_FAILURE);
        }
        break;
      case 'p':
        promiscuous = true;
        break;
      case 'd':
        display = true;
        break;
      default: /* '?' */
        std::cerr << "Unsupported option: " << std::to_string(opt) << std::endl;
        usage(EXIT_FAILURE);
    }
  }
  if(ifaces.empty() || file.empty())
  {
    usage(EXIT_FAILURE);
  }

  std::string::size_type idx = ifaces.find( ',', 0 );
  if(idx != std::string::npos)
  {
    /* list de ifaces */

    auto tokens = split(ifaces, ",");
    for(auto name : tokens)
    {
      std::cerr << "NetCap register iface name'" << name << "'" << std::endl;
      auto iface = new NetIFace(name, promiscuous);
      if(!iface->open())
      {
        std::cerr << iface->getError() << std::endl;
        delete iface;
        exit(EXIT_FAILURE);
      }
      netList.push_back(iface);
    }
  }
  else
  {
    std::cerr << "NetCap register iface name'" << ifaces << "'" << std::endl;
    auto iface = new NetIFace(ifaces, promiscuous);
    if(!iface->open())
    {
      std::cerr << iface->getError() << std::endl;
      delete iface;
      exit(EXIT_FAILURE);
    }
    netList.push_back(iface);
  }


  capfile.open(file, std::ios::out|std::ios::binary);
  if(!capfile.is_open() || capfile.fail())
  {
    std::cerr << "Unable to open the capture file '" << file << "': (" << std::to_string(errno) << ") " << strerror(errno) << std::endl;
    exit(EXIT_FAILURE);
  }
  std::cerr << "NetCap ready with PID " << std::to_string(getpid()) << std::endl;

  Net net(capfile, netList);
  if(net.process(&end, display) == -1)
  {
    std::cerr << net.getError() << std::endl;
    exit(EXIT_FAILURE);
  }
  return EXIT_SUCCESS;
}

/**
 * @brief Splits a string according to the specified regex
 * @param[in] in The input string.
 * @param[in] reg The regex.
 * @retval The result in a vector.
 */
auto split(const std::string& in, const std::string &reg) -> std::vector<std::string>
{
  // passing -1 as the submatch index parameter performs splitting
  std::regex re(reg);
  std::sregex_token_iterator first{in.begin(), in.end(), re, -1}, last;
  return {first, last};

}

/**
 * @fn static void usage(int xcode)
 * @brief usage function.
 * @param[in] xcode The exit code.
 */
static auto usage(int xcode) -> void
{
  std::cerr << "usage: netcap [options]" << std::endl;
  std::cerr << "\t--help, -h: Print this help" << std::endl;
  std::cerr << "\t--iface, -i: Interface name or comma-separated list of interfaces." << std::endl;
  std::cerr << "\t--file, -f: ThThe name of the output file." << std::endl;
  std::cerr << "\t--promiscuous, -p: Switch the interface to promiscuous mode." << std::endl;
  std::cerr << "\t--display, -d: Display the number of captured packets." << std::endl;
  exit(xcode);
}

/**
 * @fn static void sig_catch(int sig)
 * @brief Trap A signal and leave.
 * @param[in] sig the signal.
 */
static auto sig_catch(int sig) -> void
{
  exit(sig); /* call atexit */
}

/**
 * @fn static void cleanup(void)
 * @brief Soft cleanup.
 */
static auto cleanup() -> void
{
  std::uint64_t tot = 0;
  end = true;
  if(capfile.is_open())
  {
    capfile.flush();
    capfile.close();
  }
  for(auto iface : netList)
  {
    iface->close();
    std::cerr << "Captured packet " << std::to_string(iface->getCount()) << " for iface " << iface->getName() << std::endl;
    tot += iface->getCount();
    delete iface;
  }
  std::cerr << "Total packets " << std::to_string(tot) << std::endl;
}
