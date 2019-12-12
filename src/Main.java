
public class Main {

    public static void main(String[] args){
        System.out.println("doing the job lol");
        DatasetYelp dy = new DatasetYelp();
        BusinessRetrival br = new BusinessRetrival(dy);
        GUI g = new GUI(dy, br);
        g.showGUI();
    }
}
