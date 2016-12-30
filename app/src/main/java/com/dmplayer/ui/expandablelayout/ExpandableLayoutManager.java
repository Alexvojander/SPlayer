package com.dmplayer.ui.expandablelayout;

import java.util.ArrayList;
import java.util.List;

public class ExpandableLayoutManager {
    private List<ExpandableLayout> layouts;

    public ExpandableLayoutManager() {
        layouts = new ArrayList<>();
    }

    public void register(final ExpandableLayout layout) {
        layouts.add(layout);
    }

    public void collapseOthers(ExpandableLayout layout) {
        for (ExpandableLayout registeredLayout : layouts) {
            if (registeredLayout != null &&
                    registeredLayout != layout &&
                    registeredLayout.isExpanded()) {
                registeredLayout.collapse();

                break;
            }
        }
    }
}