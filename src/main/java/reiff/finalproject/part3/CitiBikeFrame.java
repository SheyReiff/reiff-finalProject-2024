package reiff.finalproject.part3;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;
import reiff.finalproject.aws.Response;

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
    private final List<GeoPosition> track = Arrays.asList();
    private final Set<Waypoint> waypoints = new HashSet<>(Arrays.asList());
    private boolean isFirstClick = true;
    private boolean waypointsLocked = false;
    private GeoPosition from;
    private GeoPosition to;

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

                if (waypoints.size() >= 4) {
                    JOptionPane.showMessageDialog(
                            CitiBikeFrame.this,
                            "Please reset your request to select new locations.",
                            "Reset Required",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }


                if (waypointsLocked) {
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

                if (isFirstClick) {

                    from = clickedPosition;
                    fromLabel.setText("From: " + clickedPosition);
                    isFirstClick = false;
                    waypoints.add(new DefaultWaypoint(from));
                    waypointPainter.setWaypoints(waypoints);
                    mapViewer.setOverlayPainter(waypointPainter);

                } else {

                    toLabel.setText("To: " + clickedPosition);
                    to = clickedPosition;
                    isFirstClick = true;
                    waypoints.add(new DefaultWaypoint(to));
                    waypointPainter.setWaypoints(waypoints);
                    mapViewer.setOverlayPainter(waypointPainter);
                    waypointsLocked = true;
                }
            }

        };
    }

    private void resetSelections() {
        fromLabel.setText("From:");
        toLabel.setText("To:");
        waypoints.clear();
        track.clear();
        waypointPainter.setWaypoints(waypoints);

        mapViewer.setOverlayPainter(null);
        waypointsLocked = false;
        mapViewer.setZoom(7);
    }

    private void handleCalculate() {

        if (waypoints.size() < 2) {
            JOptionPane.showMessageDialog(this,
                    "Please select both 'From' and 'To' points on the map.",
                    "Insufficient Points", JOptionPane.WARNING_MESSAGE);
            return;
        }

        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        SwingWorker<Response, Void> worker = new SwingWorker<>() {
            @Override
            protected Response doInBackground() {
                return controller.getRecommendedStations(from, to);
            }

            @Override
            protected void done() {
                try {

                    Response response = get();

                    GeoPosition startStation = new GeoPosition(response.getStartStation().lat,
                            response.getStartStation().lon);
                    GeoPosition endStation = new GeoPosition(response.getEndStation().lat,
                            response.getEndStation().lon);

                    List<GeoPosition> track = Arrays.asList(
                            from,
                            startStation,
                            endStation,
                            to
                    );
                    routePainter.setTrack(track);

                    waypoints.add(new DefaultWaypoint(startStation));
                    waypoints.add(new DefaultWaypoint(endStation));
                    waypointPainter.setWaypoints(waypoints);

                    List<Painter<JXMapViewer>> painters = List.of(routePainter, waypointPainter);
                    mapViewer.setOverlayPainter(new CompoundPainter<>(painters));

                    mapViewer.zoomToBestFit(Set.of(from, startStation, endStation, to), 1.0);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CitiBikeFrame.this, "Error calculating route: "
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                }
            }
        };

        worker.execute();
    }
}