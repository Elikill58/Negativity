package com.elikill58.negativity.spigot.protocols.reach;

import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.elikill58.negativity.spigot.SpigotNegativity;

public class Rect {

	protected double x1 = 0, y1 = 0, z1 = 0;
	protected double x2 = 0, y2 = 0, z2 = 0;

	public Rect(Object bb) throws Exception {
		// this(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f);
		Class<?> clss = bb.getClass();
		try {
			x1 = clss.getField("minX").getDouble(bb);
			y1 = clss.getField("minY").getDouble(bb);
			z1 = clss.getField("minZ").getDouble(bb);

			x2 = clss.getField("maxX").getDouble(bb);
			y2 = clss.getField("maxY").getDouble(bb);
			z2 = clss.getField("maxZ").getDouble(bb);
		} catch (Exception e) {
			try {
				x1 = clss.getField("a").getDouble(bb);
				y1 = clss.getField("b").getDouble(bb);
				z1 = clss.getField("c").getDouble(bb);

				x2 = clss.getField("d").getDouble(bb);
				y2 = clss.getField("e").getDouble(bb);
				z2 = clss.getField("f").getDouble(bb);
			} catch (Exception e2) {
				SpigotNegativity.getInstance().getLogger()
						.warning("Failed to find values in BoundingBox or AxisAlignedBB.");
				e2.printStackTrace();
			}
		}
	}

	public Rect(Rect r1, Rect r2, Function<TwoDouble, Double> f) {
		this(f.apply(new TwoDouble(r1.x1, r2.x1)), f.apply(new TwoDouble(r1.y1, r2.y1)),
				f.apply(new TwoDouble(r1.z1, r2.z1)), f.apply(new TwoDouble(r1.x2, r2.x2)),
				f.apply(new TwoDouble(r1.y2, r2.y2)), f.apply(new TwoDouble(r1.z2, r2.z2)));
	}

	public Rect(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.x1 = x1;
		this.y1 = x1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public Point getMin() {
		return new Point(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
	}

	public Point getMid() {
		return new Point((x1 + x2) / 2, (y1 + y2) / 2, (z1 + z2) / 2);
	}
	
	public Point getMax() {
		return new Point(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}

	public boolean isIn(double x, double y, double z) {
		return (Math.min(x1, x2) <= x && x <= Math.max(x1, x2)) && (Math.min(y1, y2) <= y && y <= Math.max(y1, y2))
				&& (Math.min(z1, z2) <= z && z <= Math.max(z1, z2));
	}
	
	public double distance(Player p) {
		Vector origin = p.getEyeLocation().toVector();
		RayTrace rayTrace = new RayTrace(p.getWorld(), origin, p.getEyeLocation().getDirection());
		//rayTrace.highlight(6.0, 0.01);
		try {
			return origin.distance(rayTrace.positionOfIntersection(getMin(), getMax(), 6.0, 0.01));
		} catch (Exception e) {
			return 0.0D;
		}
	}

	public static class TwoDouble {

		public double a, b;

		public TwoDouble(double a, double b) {
			this.a = a;
			this.b = b;
		}
	}

	@Override
	public String toString() {
		return "Rect[x1=" + x1 + ",y1=" + y1 + ",z1=" + z1 + ",then,x2=" + x2 + ",y2=" + y2 + ",z2=" + z2 + "]";
	}
}
