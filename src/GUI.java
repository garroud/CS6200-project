import org.apache.solr.common.SolrDocument;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUI {

  private DatasetYelp dy;
  private BusinessRetrival br;
  private String query;
  private JFrame frame;

  public GUI(DatasetYelp dy, BusinessRetrival br) {
    this.dy = dy;
    this.br = br;
    this.query = "";
    this.frame = new JFrame("Yelp Searcher");
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setLocationRelativeTo(null);
  }

  public void showGUI() {
    JTextField t = new JTextField();
    JButton b = new JButton("search");
    t.setBounds(10, 20, 380, 50);
    b.setBounds(400, 20, 90, 50);
    b.setBackground(Color.YELLOW);
    SearchListener sl = new SearchListener(t);
    b.addActionListener(sl);
    frame.add(b);
    frame.add(t);
    frame.setSize(500, 100);
    frame.setLayout(null);
    frame.setVisible(true);
  }

  class SearchListener implements ActionListener {
    JTextField t;

    SearchListener(JTextField t) {
      this.t = t;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      query = t.getText();
      if (query.equals("")) {
        JOptionPane.showMessageDialog(frame, "Cannot search an empty query.");
        return;
      }
      JFrame newFrame = new JFrame("Search result: "  +  query);
      newFrame.setLayout(new GridLayout(3,1));
      newFrame.setSize(1200,900);
      newFrame.setLocationRelativeTo(null);
      Map<String, List<SolrDocument>> reviewSet
              = br.generateReviewGroups(100, query);
      if (reviewSet.size() == 0) {
        JOptionPane.showMessageDialog(frame, "Empty search result.");
        return;
      }
      List<String> res = br.retrieveTopBids(reviewSet, 3);
      showBusiness(reviewSet, res, newFrame);
      newFrame.setVisible(true);
    }

    void showBusiness( Map<String, List<SolrDocument>> reviewSet, List<String> bids, JFrame window) {
      for (String bid: bids) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        String businessName = ((ArrayList<String>) dy.getBusiness(bid).get("name")).get(0);
        Double rating = ((List<Double>) dy.getBusiness(bid).get("stars")).get(0);
        JLabel title = new JLabel(businessName + " stars: " + rating);
        panel.add(title);
        List<String> topreview = reviewSet.get(bid).stream().sorted(
                (x, y) -> (((Double) y.get("computeScore")).compareTo((Double) x.get("computeScore"))))
                .map(x -> ((List<String>) x.get("text")).get(0)).collect(Collectors.toList());
        topreview = topreview.subList(0,Math.min(topreview.size(),3));
        for (String review : topreview) {
          JTextArea review1 = new JTextArea(review);
          review1.setLineWrap(true);
          review1.setWrapStyleWord(true);
          review1.setOpaque(false);
          review1.setEditable(false);
          JScrollPane scrollreview = new JScrollPane(review1);
          panel.add(scrollreview);
        }
        JPanel bidPanel = new JPanel();
        bidPanel.setLayout(new BoxLayout(bidPanel,BoxLayout.X_AXIS));

        String photoId = "no_image.png";
        try{
          photoId = ((List<String>)dy.getPhoto(bid,1).get(0).get("photo_id")).get(0);
        }
        catch (IndexOutOfBoundsException e) {
          System.err.println("no photo available for " + businessName);
        }
        if (!photoId.equals("no_image.png")) {
          photoId = photoId + ".jpg";
        }
        photoId = dy.getPhotoPath() + photoId;
        ImageIcon image = new ImageIcon(photoId);
        Image scaleImage = image.getImage();
        scaleImage = scaleImage.getScaledInstance(300,300, Image.SCALE_SMOOTH);
        image = new ImageIcon(scaleImage);
        JLabel imageLabel = new JLabel("", image, JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        bidPanel.add(panel);
        bidPanel.add(imageLabel);
        window.add(bidPanel);
      }
    }
  }

}
