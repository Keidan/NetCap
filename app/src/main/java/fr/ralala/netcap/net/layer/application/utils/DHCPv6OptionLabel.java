package fr.ralala.netcap.net.layer.application.utils;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public enum DHCPv6OptionLabel {
  UNKNOWN(-1, "Unknown"),
  CLIENT_ID(1, "Client Identifier"),
  SERVER_ID(2, "Server Identifier"),
  IA_NA(3, "Identity Association for Non-temporary Addresses"),
  IA_TA(4, "Identity Association for Temporary Addresses"),
  IAADDR(5, "Identity Association Address"),
  REQUEST(6, "Option Request"),
  PREFERENCE(7, "Preference"),
  ELAPSED_TIME(8, "Elapsed time"),
  RELAY_MSG(9, "Relay Message"),
  AUTH(11, "Authentication"),
  UNICAST(12, "Server Unicast"),
  STATUS_CODE(13, "Status code"),
  RAPID_COMMIT(14, "Rapid Commit"),
  USER_CLASS(15, "User Class"),
  VENDOR_CLASS(16, "Vendor Class"),
  VENDOR_OPTS(17, "Vendor-specific Information"),
  INTERFACE_ID(18, "Interface-Id"),
  RECONF_MSG(19, "Reconfigure Message"),
  RECONF_ACCEPT(20, "Reconfigure Accept"),
  SIP_SDNL(21, "SIP Servers Domain Name List."),
  SIP_SIP6AL(22, "SIP Servers IPv6 Address List."),
  DNS_RNS(23, "DNS Recursive Name Server."),
  DSL(24, "Domain Search List."),
  IDENTITY_ASSOCIATION_FOR_PREFIX_DELEGATION(25, "Identity Association for Prefix Delegation"),
  IA_PD_PREFIX(26, "IA_PD Prefix"),
  NIS_SERVERS(27, "NIS Servers"),
  NISP_SERVERS(28, "NIS+ Servers"),
  NIS_DOMAIN_NAME(29, "NI+ Domain name"),
  NISP_DOMAIN_NAME(30, "NIS+ Domain name"),
  SNTP_SERVER_LIST(31, "SNTP server list"),
  IRT(32, "Information Refresh Time"),
  BCMCS_CDNL(33, "BCMCS Controller Domain Name list"),
  BCMCSiCIP6AL(34, "BCMCS Controller IPv6 address list"),
  GEOCONF_CIVIC(36, "GEOCONF Civic"),
  REMOTE_ID(37, "Remote ID"),
  RAS_ID(38, "Relay Agent Subscriber-ID"),
  FQDN(39, "Fully Qualified Domain Name"),
  PANA_AA(40, "PANA Authentication Agent"),
  NEW_POSIX_TIMEZONE(41, "New Posix Timezone"),
  NEW_TZDB_TIMEZONE(42, "New TZDB Timezone"),
  ECHO_REQUEST(43, "Echo Request"),
  LQ_QUERY(44, "LQ Query"),
  CLIENT_DATA(45, "Client Data"),
  CLT_TIME(46, "CLT Time"),
  LQ_RELAY_DATA(47, "LQ Relay Data"),
  LQ_CLIENT_LINK(48, "LQ Client Link"),
  MIPV6_HN_ID_QDN(49, "MIPv6 Home Network ID FQDN"),
  MIPV6_VHNI(50, "MIPv6 Visited Home Network Information"),
  V_LOST_SERVER(51, "Variable  LoST Server"),
  CAPWAP_ACA(52, "CAPWAP Access Controller addresses"),
  RELAY_ID(53, "Relay ID"),
  OPTION_IPv6_ADDRESS_MOS(54, "OPTION-IPv6_Address-MoS"),
  OPTION_IPv6_FQDN_MOS(55, "OPTION-IPv6_FQDN-MoS"),
  NTP_SERVER(56, "NETP Server"),
  V6_ACCESS_DOMAIN(57, "V6 Access Domain"),
  SIP_UA_CS_LIST(58, "4+  SIP UA CS List"),
  BOOTFILE_URL(59, "BootFile URL"),
  BOOTFILE_PARAM(60, "BootFile Param"),
  CLIENT_ARCH_TYPE(61, "Clienbt Arch Type"),
  NII(62, "NII"),
  GEOLOCATION(63, "Geolocation"),
  AFTR_NAME(64, "AFTR Name"),
  ERP_LOCAL_DOMAIN_NAME(65, "ERP Local Domain Name"),
  RSOO(66, "OPTION_RSOO"),
  PD_EXCLUDE(67, "PD Exclude"),
  VSS(68, "Virtual Subnet Selection"),
  MIPV6_IHNI(69, "MIPv6 Identified Home Network Information"),
  MIPV6_UHNI(70, "MIPv6 Unrestricted Home Network Information"),
  MIPV6_HNP(71, "MIPv6 Home Network Prefix"),
  MIPV6_HAA(72, "MIPv6 Home Agent Address"),
  MIPV6_HA_FQDN(73, "MIPv6 Home Agent FQDN");


  private final String mText;
  private final int mNum;

  DHCPv6OptionLabel(int num, String text) {
    mNum = num;
    mText = text;
  }


  /**
   * @return the text
   */
  public String getText() {
    return mText;
  }

  /**
   * @return the num
   */
  public int getNum() {
    return mNum;
  }

  public static DHCPv6OptionLabel findByNumber(int num) {
    DHCPv6OptionLabel[] values = DHCPv6OptionLabel.values();
    for (DHCPv6OptionLabel l : values) {
      if (l.getNum() == num) return l;
    }
    return UNKNOWN;
  }
}
