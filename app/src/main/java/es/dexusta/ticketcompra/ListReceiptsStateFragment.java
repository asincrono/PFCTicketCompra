package es.dexusta.ticketcompra;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Receipt;

public class ListReceiptsStateFragment extends Fragment {
    private List<Receipt>               mReceipts;
    private HashMap<Long, List<Detail>> mReceiptDetailsMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<Receipt> getReceipts() {
        return mReceipts;
    }

    public void setReceipts(List<Receipt> receipts) {
        mReceipts = receipts;
    }

    public void setDetails(Receipt receipt, List<Detail> details) {
        if (mReceiptDetailsMap == null) {
            mReceiptDetailsMap = new HashMap<Long, List<Detail>>();
        }
        mReceiptDetailsMap.put(receipt.getId(), details);
    }

    public List<Detail> getDetails(Receipt receipt) {
        if (mReceiptDetailsMap == null) return null;
        return mReceiptDetailsMap.get(receipt.getId());
    }

}
