package example.kalmykov403.graphs;

import java.util.List;

import example.kalmykov403.graphs.model.LinkItem;
import example.kalmykov403.graphs.model.NodeItem;

public interface OnPointXYChangedListener {

    void onXYChanged(NodeItem newNode, List<LinkItem> newLinks);
}