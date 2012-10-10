package uk.ac.cam.cusf.squirrellog;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class TelephonyLog extends Logger {

    private static final String LOG_NAME = "telephony";

    public final PhoneStateListener phoneStateListener;

    protected TelephonyLog() {
        super(LOG_NAME, 0);

        this.phoneStateListener = new PhoneStateListener() {

            @Override
            public void onCellLocationChanged(CellLocation location) {
                if (location instanceof GsmCellLocation) {
                    GsmCellLocation gsmLocation = (GsmCellLocation) location;
                    int lac = gsmLocation.getLac();
                    int cid = gsmLocation.getCid();
                    String info = "GSM,";
                    info += (lac == -1) ? "?" : Integer.toHexString(lac);
                    info += ",";
                    info += (cid == -1) ? "?" : Integer.toHexString(cid);
                    append(info);
                } else if (location instanceof CdmaCellLocation) {
                    CdmaCellLocation cdmaLocation = (CdmaCellLocation) location;
                    int bid = cdmaLocation.getBaseStationId();
                    int nid = cdmaLocation.getNetworkId();
                    int sid = cdmaLocation.getSystemId();
                    int lat = cdmaLocation.getBaseStationLatitude();
                    int lon = cdmaLocation.getBaseStationLongitude();
                    String info = "CDMA,";
                    info += (bid == -1) ? "?" : Integer.toHexString(bid);
                    info += ",";
                    info += (nid == -1) ? "?" : Integer.toHexString(nid);
                    info += ",";
                    info += (sid == -1) ? "?" : Integer.toHexString(sid);
                    info += ",";
                    info += (lat == Integer.MAX_VALUE) ? "?" : lat;
                    info += ",";
                    info += (lon == Integer.MAX_VALUE) ? "?" : lon;
                    append(info);
                }
            }

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                append(stateCode(state) + "," + networkCode(networkType));
            }

            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                String info = serviceCode(serviceState.getState()) + ",";
                info += serviceState.getIsManualSelection() + ",";
                info += serviceState.getRoaming() + ",";
                info += serviceState.getOperatorNumeric() + ",";
                info += serviceState.getOperatorAlphaLong();
                append(info);
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                String info = String.valueOf(signalStrength.isGsm()) + ",";
                info += signalStrength.getGsmSignalStrength() + ",";
                info += signalStrength.getGsmBitErrorRate() + ",";
                info += signalStrength.getCdmaDbm() + ",";
                info += signalStrength.getCdmaEcio() + ",";
                info += signalStrength.getEvdoDbm() + ",";
                info += signalStrength.getEvdoEcio() + ",";
                info += signalStrength.getEvdoSnr();
                append(info);
            }

        };
    }

    private String networkCode(int networkType) {
        switch (networkType) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return "1xRTT";
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return "CDMA";
        case TelephonyManager.NETWORK_TYPE_EDGE:
            return "EDGE";
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return "EVDO_0";
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return "EVDO_A";
        case TelephonyManager.NETWORK_TYPE_GPRS:
            return "GPRS";
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return "HSDPA";
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return "HSPA";
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return "HSUPA";
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return "UMTS";
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            return "Unknown";
        default:
            return "";
        }
    }

    private String stateCode(int state) {
        switch (state) {
        case TelephonyManager.DATA_CONNECTED:
            return "Connected";
        case TelephonyManager.DATA_CONNECTING:
            return "Connecting";
        case TelephonyManager.DATA_DISCONNECTED:
            return "Disconnecting";
        case TelephonyManager.DATA_SUSPENDED:
            return "Suspended";
        default:
            return "";
        }
    }

    private String serviceCode(int state) {
        switch (state) {
        case ServiceState.STATE_EMERGENCY_ONLY:
            return "EMERGENCY_ONLY";
        case ServiceState.STATE_IN_SERVICE:
            return "IN_SERVICE";
        case ServiceState.STATE_OUT_OF_SERVICE:
            return "OUT_OF_SERVICE";
        case ServiceState.STATE_POWER_OFF:
            return "POWER_OFF";
        default:
            return "";
        }
    }

}
