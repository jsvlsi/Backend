import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Regenerates docs/EcoAccess-ER-Diagram.png from the PostgreSQL schema. */
public final class ErDiagramGenerator {
    private static final int W = 2600, H = 1850;
    private static final Font TITLE = new Font("SansSerif", Font.BOLD, 24);
    private static final Font HEADER = new Font("SansSerif", Font.BOLD, 20);
    private static final Font TEXT = new Font("SansSerif", Font.PLAIN, 17);
    private static final Font KEY = new Font("SansSerif", Font.BOLD, 16);

    private record Table(String name, int x, int y, String[] fields, Color fill) {
        int width() { return 340; }
        int height() { return 52 + fields.length * 25; }
        int left() { return x; }
        int right() { return x + width(); }
        int top() { return y; }
        int bottom() { return y + height(); }
        int midX() { return x + width() / 2; }
        int midY() { return y + height() / 2; }
    }

    public static void main(String[] args) throws Exception {
        BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE); g.fillRect(0, 0, W, H);
        g.setColor(new Color(25, 45, 65)); g.setFont(TITLE);
        g.drawString("EcoAccess - Entity Relationship Diagram", 80, 62);
        g.setFont(TEXT); g.setColor(new Color(80, 90, 100));
        g.drawString("Based on sql/01_schema.sql  |  PK = Primary Key  |  FK = Foreign Key  |  0..* = zero or many", 80, 92);

        Table passenger = new Table("passengers", 980, 150,
            new String[]{"PK  id", "     name", "UK  mobile", "     email", "     password_hash", "     points"}, new Color(216, 238, 251));
        Table staff = new Table("staff", 90, 650,
            new String[]{"PK  id", "UK  employee_id", "     name", "     password_hash", "     job_role", "     status"}, new Color(226, 242, 215));
        Table admin = new Table("admins", 2160, 150,
            new String[]{"PK  id", "     name", "UK  email", "     password_hash"}, new Color(239, 222, 205));
        Table ticket = new Table("tickets", 1740, 470,
            new String[]{"PK  pnr", "     train", "     journey_date", "     journey_time", "     station", "     platform", "     origin", "     destination"}, new Color(232, 220, 246));
        Table journey = new Table("journey_validations", 1030, 470,
            new String[]{"PK  id", "FK  passenger_id", "FK  pnr", "     valid", "     validated_at"}, new Color(216, 238, 251));
        Table location = new Table("station_locations", 90, 400,
            new String[]{"PK  station", "PK  location_name"}, new Color(250, 239, 190));
        Table booking = new Table("bookings", 820, 860,
            new String[]{"PK  id", "FK  passenger_id", "FK  staff_id (optional)", "     service", "     station", "     pickup", "     drop_location", "     service_date / time", "     fare", "     status", "     passenger_count"}, new Color(255, 238, 180));
        Table resource = new Table("resources", 90, 1050,
            new String[]{"PK  id", "UK  station + service", "     quantity"}, new Color(226, 242, 215));
        Table waste = new Table("waste_submissions", 70, 1410,
            new String[]{"PK  id", "FK  passenger_id", "     photo_path", "     status", "     reward_points", "     submitted_at", "     remark"}, new Color(247, 216, 216));
        Table coupon = new Table("coupons", 1570, 1000,
            new String[]{"PK  id", "UK  code", "FK  passenger_id", "     value", "     remaining", "     status", "     expires_at"}, new Color(227, 222, 248));
        Table redemption = new Table("redemptions", 1970, 1350,
            new String[]{"PK  id", "FK  passenger_id", "FK  coupon_code", "     points", "     coupon_value", "     status", "     redemption_date"}, new Color(239, 222, 205));
        Table cases = new Table("cases (feedback / complaints)", 980, 1450,
            new String[]{"PK  id", "FK  passenger_id", "FK  booking_id (optional)", "     type", "     rating", "     subject", "     description", "     status"}, new Color(216, 238, 251));

        // Draw relationships first, so table cards remain clear and readable.
        relation(g, passenger, journey, "1", "0..1", "validates", Side.BOTTOM, Side.TOP);
        relation(g, ticket, journey, "1", "0..*", "used for", Side.LEFT, Side.RIGHT);
        relation(g, passenger, booking, "1", "0..*", "creates", Side.BOTTOM, Side.TOP);
        relation(g, staff, booking, "1", "0..*", "assigned to", Side.BOTTOM, Side.LEFT);
        relation(g, passenger, waste, "1", "0..*", "submits", Side.BOTTOM, Side.TOP);
        relation(g, passenger, coupon, "1", "0..*", "owns", Side.RIGHT, Side.TOP);
        relation(g, passenger, redemption, "1", "0..*", "makes", Side.RIGHT, Side.TOP);
        relation(g, coupon, redemption, "1", "0..*", "referenced by", Side.RIGHT, Side.LEFT);
        relation(g, passenger, cases, "1", "0..*", "raises", Side.BOTTOM, Side.TOP);
        relation(g, booking, cases, "1", "0..*", "relates to", Side.BOTTOM, Side.TOP);

        drawTable(g, passenger); drawTable(g, staff); drawTable(g, admin); drawTable(g, ticket);
        drawTable(g, journey); drawTable(g, location); drawTable(g, booking); drawTable(g, resource);
        drawTable(g, waste); drawTable(g, coupon); drawTable(g, redemption); drawTable(g, cases);

        g.setFont(TEXT); g.setColor(new Color(90, 90, 90));
        g.drawString("Note: resources and station_locations store station names as master data; the supplied schema has no separate stations table.", 80, 1790);
        g.dispose();
        File target = new File("docs/EcoAccess-ER-Diagram.png");
        target.getParentFile().mkdirs();
        ImageIO.write(image, "png", target);
        System.out.println("Created " + target.getAbsolutePath());
    }

    private enum Side { TOP, BOTTOM, LEFT, RIGHT }
    private static Point point(Table t, Side s) {
        return switch (s) { case TOP -> new Point(t.midX(), t.top()); case BOTTOM -> new Point(t.midX(), t.bottom()); case LEFT -> new Point(t.left(), t.midY()); case RIGHT -> new Point(t.right(), t.midY()); };
    }
    private static void relation(Graphics2D g, Table a, Table b, String aCard, String bCard, String label, Side aSide, Side bSide) {
        Point p = point(a, aSide), q = point(b, bSide);
        g.setColor(new Color(95, 105, 115)); g.setStroke(new BasicStroke(2f));
        int mx = (p.x + q.x) / 2, my = (p.y + q.y) / 2;
        if (aSide == Side.TOP || aSide == Side.BOTTOM) { g.drawLine(p.x, p.y, p.x, my); g.drawLine(p.x, my, q.x, my); g.drawLine(q.x, my, q.x, q.y); }
        else { g.drawLine(p.x, p.y, mx, p.y); g.drawLine(mx, p.y, mx, q.y); g.drawLine(mx, q.y, q.x, q.y); }
        g.setFont(KEY); g.setColor(new Color(55, 65, 75));
        g.drawString(aCard, p.x + 8, p.y - 8); g.drawString(bCard, q.x + 8, q.y - 8);
        g.setFont(TEXT); g.drawString(label, mx + 8, my - 8);
    }
    private static void drawTable(Graphics2D g, Table t) {
        int w = t.width(), h = t.height();
        g.setColor(new Color(225, 225, 225)); g.fillRoundRect(t.x + 5, t.y + 6, w, h, 4, 4);
        g.setColor(t.fill); g.fillRect(t.x, t.y, w, h);
        g.setColor(new Color(45, 55, 65)); g.setStroke(new BasicStroke(1.4f)); g.drawRect(t.x, t.y, w, h); g.drawLine(t.x, t.y + 52, t.x + w, t.y + 52);
        g.setFont(HEADER); FontMetrics fm = g.getFontMetrics(); g.drawString(t.name, t.x + (w - fm.stringWidth(t.name)) / 2, t.y + 34);
        g.setFont(TEXT); int y = t.y + 78;
        for (String field : t.fields) { if (field.startsWith("PK") || field.startsWith("FK") || field.startsWith("UK")) g.setFont(KEY); else g.setFont(TEXT); g.drawString(field, t.x + 22, y); y += 25; }
    }
}
