package reiff.finalproject.part3;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class CitiBikeFrame extends JFrame {
    private final JXMapViewer mapViewer;
    private final JLabel fromLabel;
    private final JLabel toLabel;
    private final RoutePainter routePainter = new RoutePainter(new ArrayList<>());
    private final WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
    private final CitiBikeController controller;
    private final JProgressBar progressBar;

    public CitiBikeFrame() {
        setTitle("CitiBike Route Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mapViewer = new JXMapViewer();
        fromLabel = new JLabel("From:");
        toLabel = new JLabel("To:");
        controller = new CitiBikeController();
        progressBar = new JProgressBar();

        setupMapViewer();
        setupControlPanel();
    }

    private void setupMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        tileFactory.setThreadPoolSize(8);


        GeoPosition touro = new GeoPosition(40.77186915226104, -73.98834208465821);
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(touro);

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        mapViewer.addMouseListener(createMapClickListener());
        add(mapViewer, BorderLayout.CENTER);
    }

    private void setupControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(3, 2));
        JButton calculateButton = new JButton("Calculate");
        JButton resetButton = new JButton("Reset");

        controlPanel.add(fromLabel);
        controlPanel.add(new JLabel());
        controlPanel.add(toLabel);
        controlPanel.add(new JLabel());
        controlPanel.add(calculateButton);
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.SOUTH);


        progressBar.setVisible(false);
        add(progressBar, BorderLayout.NORTH);

        calculateButton.addActionListener(e -> handleCalculate());
        resetButton.addActionListener(e -> resetSelections());
    }

    private MouseAdapter createMapClickListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller.waypoints.size() >= 4) {
                    JOptionPane.showMessageDialog(
                            CitiBikeFrame.this,
                            "Please reset your request to select new locations.",
                            "Reset Required",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                if (controller.waypointsLocked) {
                    JOptionPane.showMessageDialog(
                            CitiBikeFrame.this,
                            "Waypoints are already set. Reset to choose new locations.",
                            "Waypoints Locked",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                GeoPosition clickedPosition = mapViewer.convertPointToGeoPosition(
                        new Point2D.Double(e.getX(), e.getY()));

                if (controller.isFirstClick) {
                    controller.handleFirstClick(clickedPosition);
                    fromLabel.setText("From: " + clickedPosition);
                    waypointPainter.setWaypoints(controller.waypoints);
                    mapViewer.setOverlayPainter(waypointPainter);
                } else {
                    controller.handleSecondClick(clickedPosition);
                    toLabel.setText("To: " + clickedPosition);
                    waypointPainter.setWaypoints(controller.waypoints);
                    mapViewer.setOverlayPainter(waypointPainter);
                }
            }
        };
    }

    private void handleCalculate() {
        if (controller.waypoints.size() < 2) {
            JOptionPane.showMessageDialog(this,
                    "Please select both 'From' and 'To' points on the map.",
                    "Insufficient Points", JOptionPane.WARNING_MESSAGE);
            return;
        }

        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        controller.calculateRoute(controller.from, controller.to, this);
    }

    public void updateMap(List<GeoPosition> track, Set<Waypoint> waypoints, GeoPosition from,
                          GeoPosition startStation, GeoPosition endStation, GeoPosition to) {
        routePainter.setTrack(track);
        waypointPainter.setWaypoints(waypoints);

        List<Painter<JXMapViewer>> painters = List.of(routePainter, waypointPainter);
        mapViewer.setOverlayPainter(new CompoundPainter<>(painters));

        mapViewer.zoomToBestFit(new HashSet<>(Arrays.asList(from, startStation, endStation, to)), 1.0);
    }
    public void showCalculatingError(String message) {
        JOptionPane.showMessageDialog(this,
                message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void hideProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
    }

    private void resetSelections() {
        controller.resetSelectionsController();
        fromLabel.setText("From:");
        toLabel.setText("To:");
        mapViewer.setOverlayPainter(null);
        mapViewer.setZoom(7);
    }
}