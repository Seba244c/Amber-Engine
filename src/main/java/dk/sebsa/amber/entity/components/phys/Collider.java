package dk.sebsa.amber.entity.components.phys;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;

public class Collider extends Component {
	public boolean isTrigger = false; 
	
	static List<Collider> colliders = new ArrayList<Collider>();
	private static List<Collider> movers = new ArrayList<Collider>();
	private static List<Collider> solids = new ArrayList<Collider>();
		
	public static void clear() {colliders.clear();}
	public static void clearFrame() {movers.clear(); solids.clear();}
	
	public void init() {}
	
	public void onWillRender() {
		addFrameCollider();
	}
	
	public void addFrameCollider() {		
		if(entity.isDirty()) movers.add(this);
		else solids.add(this);
	}
	
	public static void compareCollisions() {
		for(int m = 0; m < movers.size(); m++) {
			Collider mover = movers.get(m);
			for(int s = 0; s < solids.size(); s++) {
				Collider solid = solids.get(s);
				if(mover.equals(solid)) continue;
				
				if(mover instanceof BoundsCollider && solid instanceof BoundsCollider) compareCollision((BoundsCollider) mover, (BoundsCollider) solid);
				
				else if(mover instanceof AABBCollider && solid instanceof AABBCollider) compareCollision((AABBCollider) mover, (AABBCollider) solid);
				else if(mover instanceof AABBCollider && solid instanceof CircleCollider) compareCollision((AABBCollider) mover, (CircleCollider) solid, 0);
				else if(mover instanceof AABBCollider && solid instanceof LineCollider) compareCollision((AABBCollider) mover, (LineCollider) solid);
				
				else if(mover instanceof CircleCollider && solid instanceof CircleCollider) compareCollision((CircleCollider) mover, (CircleCollider) solid);
				else if(mover instanceof CircleCollider && solid instanceof AABBCollider) compareCollision((AABBCollider) solid, (CircleCollider) mover, 1);
				else if(mover instanceof CircleCollider && solid instanceof LineCollider) compareCollision((CircleCollider) mover, (LineCollider) solid);
			}
			mover.entity.resetDirty();
			solids.add(mover);
		}
	}
	
	private static void compareCollision(CircleCollider mover, CircleCollider solid) {
		Vector2f moverCenter = mover.entity.getPosition().add((mover.anchor));
		Vector2f solidCenter = solid.entity.getPosition().add((solid.anchor));
		
		float distance = solidCenter.sub(moverCenter).length();
		
		if(distance >= mover.radius + solid.radius) return;
		
		//Collision point = solid - (direction to mover * radius);
		if(mover.isTrigger || solid.isTrigger){mover.entity.callTriggerCallback(); solid.entity.callTriggerCallback();}
		else {
			mover.entity.callCollisionCallback();
			solid.entity.callCollisionCallback();
			mover.entity.move(moverCenter.sub(solidCenter).normalized().mul((mover.radius + solid.radius) - distance));
		}
	}
	
	private static void compareCollision(AABBCollider mover, AABBCollider solid) {
		Vector2f moverCorner = new Vector2f(mover.entity.getPosition().add(mover.anchor));
		Rect moverRect = new Rect(moverCorner, mover.size);
		
		Vector2f solidCorner = new Vector2f(solid.entity.getPosition().add(solid.anchor));
		Rect solidRect = new Rect(solidCorner, solid.size);
		
		Rect overlap = moverRect.getIntersection(solidRect);
		if(overlap != null) {
			if(mover.isTrigger || solid.isTrigger){mover.entity.callTriggerCallback(); solid.entity.callTriggerCallback();}
			else {
				mover.entity.callCollisionCallback();
				solid.entity.callCollisionCallback();
				
				if(overlap.width < overlap.height) {
					if(moverRect.x < overlap.x) mover.entity.move(new Vector2f(-overlap.width, 0));
					else mover.entity.move(new Vector2f(overlap.width, 0));
				} else {
					if(moverRect.y < overlap.y) mover.entity.move(new Vector2f(0, -overlap.height));
					else mover.entity.move(new Vector2f(0, overlap.height));
				}
			}
		}
	}
	
	//Bounds Collider vs Bounds Collider
	private static void compareCollision(BoundsCollider mover, BoundsCollider solid) {
		SpriteRenderer moverRenderer = (SpriteRenderer) mover.entity.getComponent("SpriteRenderer");
		SpriteRenderer solidRenderer = (SpriteRenderer) solid.entity.getComponent("SpriteRenderer");
		if(moverRenderer == null || solidRenderer == null) return;
		if(moverRenderer.sprite == null || solidRenderer.sprite == null) return;
		
		Vector2f[] poly1 = moverRenderer.getCorners();
		Vector2f[] poly2 = solidRenderer.getCorners();
		
		Rect poly1Bounds = new Rect(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		Rect poly2Bounds = new Rect(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		for(int i = 0; i < 4; i++) {
			poly1Bounds.x = Math.min(poly1Bounds.x, poly1[i].x);
			poly2Bounds.x = Math.min(poly2Bounds.x, poly2[i].x);
			
			poly1Bounds.width = Math.max(poly1Bounds.width, poly1[i].x);
			poly2Bounds.width = Math.max(poly2Bounds.width, poly2[i].x);
			
			poly1Bounds.y = Math.min(poly1Bounds.y, poly1[i].y);
			poly2Bounds.y = Math.min(poly2Bounds.y, poly2[i].y);
			
			poly1Bounds.height = Math.max(poly1Bounds.height, poly1[i].y);
			poly2Bounds.height = Math.max(poly2Bounds.height, poly2[i].y);
		}
		poly1Bounds.width = poly1Bounds.width - poly1Bounds.x;
		poly2Bounds.width = poly2Bounds.width - poly2Bounds.x;
		poly1Bounds.height = poly1Bounds.height - poly1Bounds.y;
		poly2Bounds.height = poly2Bounds.height - poly2Bounds.y;
		
		Rect overlap = poly1Bounds.getIntersection(poly2Bounds);
		if(overlap != null){
			if(mover.isTrigger || solid.isTrigger){mover.entity.callTriggerCallback(); solid.entity.callTriggerCallback();}
			else {
				mover.entity.callCollisionCallback();
				solid.entity.callCollisionCallback();
				
				if(overlap.width < overlap.height) {
					if(poly1[0].x < overlap.x) mover.entity.move(new Vector2f(-overlap.width, 0));
					else mover.entity.move(new Vector2f(overlap.width, 0));
				} else {
					if(poly1[0].y < overlap.y) mover.entity.move(new Vector2f(0, -overlap.height));
					else mover.entity.move(new Vector2f(0, overlap.height));
				}
			}
		}
	}
	
	private static void compareCollision(AABBCollider mover, LineCollider solid) {
		List<Vector2f> corners = mover.getCorners();
		Vector2f start = corners.get(0);
		Vector2f center = start.add(corners.get(2).sub(start).mul(0.5f));
		
		Vector2f lineStart = solid.getWorldStart();
		Vector2f lineEnd = solid.getWorldEnd();
		
		int collided = 0;
		for(int i = 0; i < 4; i++) {
			Vector2f corner = corners.get(i);
			if(Vector2f.intersection(center, corner, lineStart, lineEnd) != null) {
				Vector2f projection = corner.project(lineStart, lineEnd);
				mover.entity.move(projection.sub(corner));
			}
		}
		if(collided == 0) {
			for(int i = 0; i < 4; i++) {
				Vector2f corner = corners.get(i);
				Vector2f next = corners.get((i + 1) % corners.size());
				
				if(Vector2f.intersection(corner, next, lineStart, lineEnd) != null) {
					Rect r = new Rect(start, corners.get(2).sub(corners.get(0)));
					if(r.contains(lineStart)) {
						Vector2f projection = lineStart.project(corner, next);
						mover.entity.move(lineStart.sub(projection));
					} else if(r.contains(lineEnd)) {
						Vector2f projection = lineEnd.project(corner, next);
						mover.entity.move(lineEnd.sub(projection));
					}
				}
			}
		}
	}
	
	private static void compareCollision(AABBCollider aabb, CircleCollider circle, int moverStatus) {
		Vector2f center = circle.center();
		Vector2f closestPoint = center.project(aabb.getCorners());
		float distance = Vector2f.distance(center, closestPoint);
		
		if(distance < circle.radius) {
			if(aabb.isTrigger || circle.isTrigger){aabb.entity.callTriggerCallback(); circle.entity.callTriggerCallback();}
			else {
				aabb.entity.callCollisionCallback();
				circle.entity.callCollisionCallback();
				
				Vector2f offset = Vector2f.direction(center, closestPoint).mul(circle.radius - distance);
				if(moverStatus == 0) aabb.entity.move(offset);
				else circle.entity.move(offset.neg());
			}
		}
	}
	
	private static void compareCollision(CircleCollider circle, LineCollider line) {
		Vector2f start = line.getWorldStart();
		Vector2f end = line.getWorldEnd();
		Vector2f center = circle.center();
		
		Vector2f proj = center.project(start, end);
		float distance = Vector2f.distance(center, proj);
		if(distance < circle.radius) {
			if(line.isTrigger || circle.isTrigger){line.entity.callTriggerCallback(); circle.entity.callTriggerCallback();}
			else {
				line.entity.callCollisionCallback();
				circle.entity.callCollisionCallback();
				
				circle.entity.move((Vector2f.direction(center, proj).mul(circle.radius - distance)).neg());
			}
		}
	}
}
