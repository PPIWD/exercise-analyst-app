package pl.ppiwd.exerciseanalyst.services.metamotion.helpers;

import com.mbientlab.metawear.MetaWearBoard;

public interface ConnectionEventListener {
    void onConnectionSuccessful(MetaWearBoard board);

    void onConnectionFailed();
}
