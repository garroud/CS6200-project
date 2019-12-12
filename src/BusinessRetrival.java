import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BusinessRetrival {
  private DatasetYelp dataset;

  public BusinessRetrival(DatasetYelp y) {
    this.dataset = y;
  }

  public Map<String, List<SolrDocument>> generateReviewGroups(int size, String query) {
    List<SolrDocument> retrivedReview = dataset.getReviews(Math.min(10000, size * 500), query);
    //Map all the reviews to their business id.
    Map<String, List<SolrDocument>> bid2review = new HashMap<>();
    for (int i = 0; i < retrivedReview.size(); ++ i) {
      SolrDocument d = retrivedReview.get(i);
      // put rank into d
      d.put("rank", i);
      String bid = ((List<String>) d.get("business_id")).get(0);
      if(!bid2review.containsKey(bid)) {
        bid2review.put(bid, new LinkedList<>());
      }
      bid2review.get(bid).add(d);
    }
    return bid2review;
  }

  public List<String> retrieveTopBids(Map<String,  List<SolrDocument>> bid2review, int size) {

    List<String> topbids = bid2review.keySet().stream()
            .sorted((x,y)->rankingScore(y,bid2review).compareTo(rankingScore(x,bid2review)))
            .collect(Collectors.toList()).subList(0,Math.min(size * 2, bid2review.size()));
//    topbids.sort((x,y)->
//            ((List<Double>) dataset.getBusiness(y).get("stars")).get(0).compareTo(
//                    ((List<Double>) dataset.getBusiness(x).get("stars")).get(0)
//            ));
    topbids = topbids.subList(0,Math.min(size, bid2review.size()));
    return topbids;
  }

  private Double rankingScore(String bid, Map<String, List<SolrDocument>> bid2review) {
    double finalScore = 0.5 * ((List<Double>) dataset.getBusiness(bid).get("stars")).get(0);
    for (SolrDocument d : bid2review.get(bid)) {
      double relevanceScore = (Float) d.get("score") * 1.0;
      Date time = ((List<Date>)d.get("date")).get(0);
      Long diff = Math.abs(time.getTime() - System.currentTimeMillis());
      diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
      double timeDiff = diff / 365.0;
      double timeDepreciation = Math.exp(-Math.log(2) * timeDiff);
      double credibility = ((ArrayList<Long>)d.get("useful")).get(0) +
              ((ArrayList<Long>)d.get("funny")).get(0) +
              ((ArrayList<Long>)d.get("cool")).get(0) + 1.0;
      double computeScore = relevanceScore * timeDepreciation * credibility;
      d.put("computeScore", computeScore);
      finalScore += computeScore;
    }
    return finalScore;
  }
}
