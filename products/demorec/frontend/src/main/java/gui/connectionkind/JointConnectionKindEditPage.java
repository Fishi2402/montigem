/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import demorec.DemoRecManager;
import demorec.ExternalPhoto;
import demorec.JointConnection;
import demorec.State;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class JointConnectionKindEditPage extends JointConnectionKindEditPageTOP {

  /** Index into the connection list when iterating in batch mode. -1 = not in batch mode. */
  private int batchIndex = -1;
  private boolean batchMode = false;

  // ── Connection creation ──────────────────────────────────────────────────

  /**
   * Creates a new JointConnection with a default HEALTHY condition and adds it
   * to the current connectionKind. It immediately appears in the iterate list.
   */
  public Consumer<Void> createConnection() {
    return $ -> {
      JointConnection connection = DemoRecManager
          .jointConnectionBuilder()
          .condition(State.HEALTHY)
          .build()
          .get();
      this.getConnectionKind().addJointConnection(connection);
    };
  }

  // ── Manual selection ─────────────────────────────────────────────────────

  /**
   * Selects a specific connection for position picking.
   * Exits batch mode if it was active.
   */
  @Override
  public Consumer<Void> selectConnection(JointConnection connection) {
    return $ -> {
      this.setSelectedConnectionId(String.valueOf(connection.getGemId()));
      this.batchMode = false;
      this.batchIndex = -1;
    };
  }

  // ── Batch iteration ──────────────────────────────────────────────────────

  /**
   * Starts iterating through all connections in order.
   * Each time the user picks a position the next connection is automatically selected.
   */
  @Override
  public Consumer<Void> startBatch() {
    return $ -> {
      List<JointConnection> connections = this.getConnectionKind().getJointConnectionList();
      if (connections.isEmpty()) {
        return;
      }
      this.batchMode = true;
      this.batchIndex = 0;
      this.setSelectedConnectionId(String.valueOf(connections.get(0).getGemId()));
    };
  }

  /** Advances to the next connection in batch mode, or ends the batch when all are done. */
  private void advanceBatch() {
    List<JointConnection> connections = this.getConnectionKind().getJointConnectionList();
    this.batchIndex++;
    if (this.batchIndex < connections.size()) {
      this.setSelectedConnectionId(String.valueOf(connections.get(this.batchIndex).getGemId()));
    } else {
      this.batchMode = false;
      this.batchIndex = -1;
      this.setSelectedConnectionId("");
    }
  }

  // ── Event handlers ───────────────────────────────────────────────────────

  /**
   * Called when the user picks a position on the PositionPicker.
   * The x/y/z coordinates are already written to connection.xPos/yPos/zPos via
   * two-way binding before this callback fires.
   * In batch mode the next connection is selected automatically.
   */
  @Override
  public Consumer<String> onPositionPicked(JointConnection connection) {
    return (String pickUrl) -> {
      ExternalPhoto photo = DemoRecManager.externalPhotoBuilder().externalUrl(pickUrl).build().get();
      connection.addPhoto(photo);
      if (this.batchMode) {
        this.advanceBatch();
      }
    };
  }

  /**
   * Called when the user selects an image in the ImageScroller.
   * Updates the image shown in the PositionPicker.
   */
  public Consumer<String> onImageSelected() {
    return (String imageUrl) -> {
      this.setSelectedImageUrl(imageUrl);
    };
  }

  // ── Position picker visibility ───────────────────────────────────────────

  /** Toggles the position picker panel between visible and hidden. */
  public Consumer<Void> togglePositionPicker() {
    return $ -> {
      this.setShowPositionPicker(!this.isShowPositionPicker());
    };
  }

  /** Label for the toggle button reflects current visibility state. */
  public String posPickerToggleLabel() {
    return this.isShowPositionPicker()
        ? "▲ Hide Position Picker"
        : "▼ Show Position Picker";
  }

  // ── Guards & labels ──────────────────────────────────────────────────────

  /** Returns true when connection is the currently active one for position picking. */
  @Override
  public Boolean isSelected(JointConnection connection) {
    String id = this.getSelectedConnectionId();
    return id != null && !id.isEmpty() && id.equals(String.valueOf(connection.getGemId()));
  }

  @Override
  public Boolean isNotSelected(JointConnection connection) {
    String id = this.getSelectedConnectionId();
    return !(id != null && !id.isEmpty() && id.equals(String.valueOf(connection.getGemId())));
  }

  @Override
  public Consumer<Void> toggleShowConditions() {
    return $-> {
      this.setShowConnectionConditions(!this.isShowConnectionConditions());
    };
  }

  @Override
  public Consumer<Void> toggleShowPositions() {
    return $-> {
      this.setShowConnectionPositions(!this.isShowConnectionPositions());
    };
  }

  @Override
  public Consumer<Void> toggleShowPhotos() {
    return $-> {
      this.setShowConnectionPhotos(!this.isShowConnectionPhotos());
    };
  }

  /** Label for the per-row select button — highlights the currently selected connection. */
  /*@Override
  public String getSelectLabel(JointConnection connection) {
    return this.isSelected(connection) ? ">> Selected <<" : "Select";
  }*/
}
