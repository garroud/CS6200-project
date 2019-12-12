import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DatasetYelp {
  private SolrClient clientReview;
  private SolrClient clientBusiness;
  private SolrClient clientPhoto;
  private final String photoPath = "/Users/xiaoruichao/Documents/information_retrival/yelp_photos/photos/";
  DatasetYelp(){
    String reviewSetName = "YelpReviews";
    String businessSetName = "YelpBusiness";
    String photoSetName = "YelpPhotos";
    clientReview = new HttpSolrClient.Builder(
            "http://localhost:8983/solr/"+reviewSetName).build();
    clientBusiness = new HttpSolrClient.Builder(
            "http://localhost:8983/solr/"+businessSetName).build();
    clientPhoto = new HttpSolrClient.Builder(
            "http://localhost:8983/solr/"+photoSetName).build();
  }


  String simpleQueryParser(String content) {
    String q = content.toLowerCase().replaceAll("\\W"," ");
    q = q.replaceAll("\\s+", " ");
    System.out.println(q);
    return q;
  }

  // get reviews given a query
  public List<SolrDocument> getReviews(int setSize, String query) {
    try {
      SolrQuery solrq = new SolrQuery();
      query = simpleQueryParser(query);
      solrq.setQuery("text:\"" + query + "\"");
      solrq.setFields("review_id","business_id", "text", "score", "rate", "star", "useful", "funny", "cool", "date");
      solrq.setRows(setSize);
      solrq.setSort("score", SolrQuery.ORDER.desc);
      QueryResponse response = clientReview.query(solrq);
      List<SolrDocument> res = response.getResults();
      Set<String> reviewId = new HashSet<>();
      res = res.stream().filter(x->reviewId.add(((List<String>)x.get("review_id"))
              .get(0))).collect(Collectors.toList());
      return res;
    }

    catch (Exception e) {
      System.err.println(e.getMessage());
      return new LinkedList<>();
    }
  }

  // return the business info given an businessId
  public SolrDocument getBusiness(String businessId) {
    try {
      SolrQuery solrq = new SolrQuery();
      solrq.setQuery("business_id:\"" + businessId + "\"");
      solrq.setFields("business_id", "name", "stars", "score");
      solrq.setRows(1);
      QueryResponse response = clientBusiness.query(solrq);
      List<SolrDocument> res = response.getResults();
      return res.get(0);
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      return null;
    }
  }

  public List<SolrDocument> getPhoto(String businessId, int size) {
    try {
      SolrQuery solrq = new SolrQuery();
      solrq.setQuery("business_id:\"" + businessId +"\"");
      solrq.setFields("business_id", "photo_id");
      solrq.setRows(size);
      QueryResponse response= clientPhoto.query(solrq);
      List<SolrDocument> res = response.getResults();
      return res;
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      return new LinkedList<>();
    }
  }

  public String getPhotoPath() {
    return photoPath;
  }
}
