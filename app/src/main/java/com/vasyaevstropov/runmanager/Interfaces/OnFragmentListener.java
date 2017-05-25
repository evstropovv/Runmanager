package com.vasyaevstropov.runmanager.Interfaces;

import android.location.Location;

/**
 * Created by Вася on 21.05.2017.
 */

public interface OnFragmentListener  {

    void bottonSheetBehaviorListener(Boolean isOpen);

    public void updateLocation(final Location location);
}
