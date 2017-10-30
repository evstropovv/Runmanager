package com.vasyaevstropov.runmanager.Interfaces;

import android.location.Location;


public interface OnFragmentListener  {

    void bottonSheetBehaviorListener(Boolean isOpen);

    public void updateLocation(final Location location);
}
