package org.voicemessanger.server.main;
import java.util.Comparator;

public class SortUsernameThread implements Comparator<InfoUsernameFromThread> {

    @Override
    public int compare(InfoUsernameFromThread e1, InfoUsernameFromThread e2) {
        return e1.getName().compareTo(e2.getName());
    }


}
