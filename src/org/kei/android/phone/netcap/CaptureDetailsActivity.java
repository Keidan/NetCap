package org.kei.android.phone.netcap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.application.DNS;
import org.kei.android.phone.jni.net.layer.application.DNSEntry;
import org.kei.android.phone.jni.net.layer.internet.IGMP;
import org.kei.android.phone.jni.net.layer.internet.IPv4;
import org.kei.android.phone.jni.net.layer.internet.IPv6;
import org.kei.android.phone.jni.net.layer.link.ARP;
import org.kei.android.phone.jni.net.layer.link.Ethernet;
import org.kei.android.phone.jni.net.layer.transport.TCP;
import org.kei.android.phone.jni.net.layer.transport.UDP;
import org.kei.android.phone.netcap.listview.ExpandableListAdapter;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

/**
 *******************************************************************************
 * @file CaptureDetailsActivity.java
 * @author Keidan
 * @date 30/09/2015
 * @par Project NetCap
 *
 * @par Copyright 2015 Keidan, all right reserved
 *
 *      This software is distributed in the hope that it will be useful, but
 *      WITHOUT ANY WARRANTY.
 *
 *      License summary : You can modify and redistribute the sources code and
 *      binaries. You can send me the bug-fix
 *
 *      Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class CaptureDetailsActivity extends Activity {
  
  private ExpandableListAdapter listAdapter;
  private ExpandableListView expListView;
  private List<String> listDataHeader;
  private HashMap<String, List<String>> listDataChild;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_capturedetails);
    Fx.updateTransition(this, true);
    setTitle(getResources().getText(R.string.app_name) + " - " + ApplicationCtx.getAppId(this));

    // get the listview
    expListView = (ExpandableListView) findViewById(R.id.detailsELV);

    // preparing list data
    prepareListData();

    listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

    // setting list adapter
    expListView.setAdapter(listAdapter);
    
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Fx.updateTransition(this, false);
    finish();
  }

  /*
   * Preparing the list data
   */
  private void prepareListData() {
    listDataHeader = new ArrayList<String>();
    listDataChild = new HashMap<String, List<String>>();
    Layer layers = ApplicationCtx.getAppLayer(this);
    Layer layer = null;
    /* get headers */
    do {
      if (layers != null) {
        layer = layers;
        List<String> list = new ArrayList<String>();
        switch(layer.getLayerType()) {
          case Layer.TYPE_ETHERNET:
            Ethernet eth = (Ethernet)layer;
            list.add("  Source: " + eth.getSource());
            list.add("  Destination: " + eth.getDestination());
            list.add("  Type: " + String.format("0x%04d", eth.getProto()));
            break;
          case Layer.TYPE_ARP:
            ARP arp = (ARP)layer;
            list.add("  Hardware type: 0x" + String.format("%04x", arp.getFormatOfHardwareAddress()));
            list.add("  Protocol type: 0x" + String.format("%04x", arp.getFormatOfProtocolAddress()));
            list.add("  Hardware size: " + String.format("%x", arp.getLengthOfHardwareAddress()));
            list.add("  Protocol size: " + String.format("%x", arp.getLengthOfProtocolAddress()));
            list.add("  Opcode: " + (arp.getOpcode() == ARP.REPLY ? "reply" : (arp.getOpcode() == ARP.REQUEST ? "request" : "unknown")));
            if(arp.getSenderHardwareAddress() != null) {
              list.add("  Sender MAC address: " + arp.getSenderHardwareAddress());
              list.add("  Sender IP address: " + arp.getSenderIPAddress());
              list.add("  Target MAC address: " + arp.getTargetHardwareAddress());
              list.add("  Target IP address: " + arp.getTargetIPAddress());
            }
            break;
          case Layer.TYPE_IPv4:
            IPv4 ip4 = (IPv4)layer;
            list.add("  Version: 4");
            list.add("  Header length: " + ip4.getHeaderLength() + " bytes");
            list.add("  Differentiated Services Field:");
            list.add("    Total Length: " + ip4.getTotLength());
            list.add("    Identification: 0x" + String.format("%04x", ip4.getIdent()) + " (" + ip4.getIdent() + ")");
            list.add("  Flags: ");
            list.add("    " + (ip4.isReservedBit() ? "1" : "0") + "... Reserved bit: " + (ip4.isReservedBit() ? "Set" : "Not Set"));
            list.add("    ." + (ip4.isDontFragment() ? "1" : "0") + ".. Don't fragment: " + (ip4.isDontFragment() ? "Set" : "Not Set"));
            list.add("    .." + (ip4.isMoreFragments() ? "1" : "0") + ". More fragments: " + (ip4.isMoreFragments() ? "Set" : "Not Set"));
            list.add("  Fragment offset: " + ip4.getFragOff());
            list.add("  Time to live: " + ip4.getTTL());
            list.add("  Protocol: " + ip4.getProtocol());
            list.add("  Header checksum: 0x" + String.format("%04x", ip4.getChecksum()));
            list.add("  Source: " + ip4.getSource());
            list.add("  Destination: " + ip4.getDestination());
            break;
          case Layer.TYPE_IPv6:
            IPv6 ip6 = (IPv6)layer;
            list.add("  Version: 6");
            list.add("  Priority: " + ip6.getPriority());
            list.add("  Flowlabel: 0x" + String.format("%02x%02x%02x", ip6.getFlowLbl_1(), ip6.getFlowLbl_2(), ip6.getFlowLbl_3()));
            list.add("  Payload length: " + ip6.getPayloadLen());
            list.add("  Next header: " + ip6.getNexthdr());
            list.add("  Hop limit: " + ip6.getHopLimit());
            list.add("  Source: " + ip6.getSource());
            list.add("  Destination: " + ip6.getDestination());
            break;
          case Layer.TYPE_UDP:
            UDP udp = (UDP)layer;
            list.add("  Source port: " + udp.getSource());
            list.add("  Destination port: " + udp.getDestination());
            list.add("  Length: " + udp.getLength());
            list.add("  Checksum: 0x" + String.format("%04x", udp.getChecksum()));
            break;
          case Layer.TYPE_TCP:
            TCP tcp = (TCP)layer;
            list.add("  Source port: " + tcp.getSource());
            list.add("  Destination port: " + tcp.getDestination());
            list.add("  Sequence number: " + tcp.getSeq());
            list.add("  Acknowledgement number: " + tcp.getAckSeq());
            list.add("  Flags:");
            list.add("    " + (tcp.isCWR() ? "1" : "0") + "... .... = Congestion Window Reduced (CWR): " + (tcp.isCWR() ? "Set" : "Not Set"));
            list.add("    ." + (tcp.isECE() ? "1" : "0") + ".. .... = ECN-Echo: " + (tcp.isECE() ? "Set" : "Not Set"));
            list.add("    .." + (tcp.isURG() ? "1" : "0") + ". .... = Urgent: " + (tcp.isURG() ? "Set" : "Not Set"));
            list.add("    ..." + (tcp.isACK() ? "1" : "0") + " .... = Acknowledgement: " + (tcp.isACK() ? "Set" : "Not Set"));
            list.add("    .... " + (tcp.isPSH() ? "1" : "0") + "... = Push: " + (tcp.isPSH() ? "Set" : "Not Set"));
            list.add("    .... ." + (tcp.isRST() ? "1" : "0") + ".. = Reset: " + (tcp.isRST() ? "Set" : "Not Set"));
            list.add("    .... .." + (tcp.isSYN() ? "1" : "0") + ". = Syn: " + (tcp.isSYN() ? "Set" : "Not Set"));
            list.add("    .... ..." + (tcp.isFIN() ? "1" : "0") + " = Fin: " + (tcp.isFIN() ? "Set" : "Not Set"));
            list.add("  Window size: " + tcp.getWindow());
            list.add("  Checksum: 0x" + String.format("%04x", tcp.getCheck()));
            list.add("  Urg ptr: " + tcp.getUrgPtr());
            break;
          case Layer.TYPE_IGMP:
            IGMP igmp = (IGMP)layer;
            if(igmp.getType() == IGMP.QUERY)
              list.add("  Type: Membership Query (0x" + String.format("%02x", igmp.getType()) + ")");
            else
              list.add("  Type: Membership Report (0x" + String.format("%02x", igmp.getType()) + ")");
            list.add("  Max Response Time: " + ((float)(igmp.getMaxRespTime() / 10)) + " sec (0x" + String.format("%02x", igmp.getMaxRespTime()) + ")");
            list.add("  Header checksum: 0x" + String.format("%04x", igmp.getChecksum()));
            list.add("  Multicast address: " + igmp.getGroupAdress());
            if(igmp.getNumberOfSources() != 0) {
              list.add("  Num Src: " + igmp.getNumberOfSources());
              for(String s : igmp.getSourceAdress()) list.add("  Source Address: " + s);
            }
            break;
          case Layer.TYPE_DNS:
            DNS dns = (DNS)layer;
            list.add("  Transaction ID: 0x" + String.format("%04x", dns.getID()));
            list.add("  Flags:");
            list.add("    RD: " + (dns.isRD() ? "Set" : "Not Set"));
            list.add("    TC: " + (dns.isTC() ? "Set" : "Not Set"));
            list.add("    AA: " + (dns.isAA() ? "Set" : "Not Set"));
            list.add("    OPCODE: 0x" + String.format("%04x", dns.getOpcode()));
            list.add("    QR: " + (dns.isQR() ? "Set" : "Not Set"));
            list.add("    RCODE: 0x" + String.format("%04x", dns.getRCode()));
            list.add("    ZERO: " + (dns.isZero() ? "Set" : "Not Set"));
            list.add("    RA: " + (dns.isRA() ? "Set" : "Not Set"));
            list.add("  Questions: " + dns.getQCount());
            list.add("  Answer RRs: " + dns.getAnsCount());
            list.add("  Authority RRs: " + dns.getAuthCount());
            list.add("  Additional RRs: " + dns.getAddCount());
            if(!dns.getQueries().isEmpty()) {
              list.add("  Queries");
              for(DNSEntry e : dns.getQueries()) {
                list.add("    Name: " + e.getName());
                list.add("    Type: " + e.getTypeString());
                list.add("    Class: " + e.getClazzString());
              }
            }
            addDNSEntry("Answer", dns.getAnswers(), list);
            addDNSEntry("Authority", dns.getAuthorities(), list);
            addDNSEntry("Additional", dns.getAdditionals(), list);
            break;
          case Layer.TYPE_PAYLOAD:
            Payload payload = (Payload)layer;
            byte [] buffer = payload.getDatas();
            List<String> lines = NetworkHelper.formatBuffer(buffer);
            for(String s : lines) list.add(s);
            break;
        }
        listDataHeader.add(layer.getFullName());
        listDataChild.put(layer.getFullName(), list);
      }
    } while ((layers = layers.getNext()) != null);
  }
  
  private void addDNSEntry(final String label, final List<DNSEntry> entries, final List<String> list) {
    if(!entries.isEmpty()) {
      list.add("  " + label);
      for(DNSEntry e : entries) {
        list.add("    Name: 0x" + String.format("%04x", e.getNameOffset()));
        list.add("    Type: " + e.getTypeString());
        list.add("    Class: " + e.getClazzString());
        list.add("    Time to live: " + ((int)e.getTTL() / 60) + " minutes, " + ((int)e.getTTL() % 60) + " seconds");
        list.add("    Data length: " + e.getDataLength());
        list.add("    Addr: " + e.getAddress());
      }
    }
  }
}
