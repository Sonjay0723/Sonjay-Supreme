package implements4b;
import robocode.*;
import java.awt.Color;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * SonjaySupreme - a robot by (your name here)
 */
public class SonjaySupreme extends AdvancedRobot
{
	boolean tracking = false;
	boolean wall;
	boolean peek;
	double movement;
	byte direction = 1;
	String follow;
	/**
	 * run: SonjaySupreme's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:
		wall = true;

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setColors(Color.red, Color.black, Color.black);
		movement = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		peek = false;
		
		turnLeft(getHeading() % 90);
		ahead(movement);
		peek = true;
		turnGunRight(90);
		turnRight(90);
		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			if (wall)
			{
				if (getGunHeading() != (getHeading() + 90))
					turnGunRight(getHeading() - getGunHeading() + 90);
				peek = true;
				ahead(movement);
				peek = false;
				turnRight(90);
			}
			else
			{
				if (tracking)
					ahead(300 * direction);
				else
				{
					setAhead(200 * direction);
					turnGunRight(360);
				}
				execute();
			}
		}
	}
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like\
		if (wall)
		{
			if (getEnergy() > 20 && getGunHeat() == 0)
				fire(Math.min(500 / e.getDistance(), 3));
			if (peek)
				scan();
		}
		else
		{
			double power = Math.min(500 / e.getDistance(), 3);
			if (e.getEnergy() < 50)
			{
				tracking = true;
				direction = 1;
				setTurnRight(e.getBearing());
				setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
				setAhead(100);
			}
			else
			{
				tracking = false;
				setTurnRight(e.getBearing() + 90);
				setTurnGunRight(normalizeBearing(getHeading() - getGunHeading() - 90));
				if (getTime() % 20 == 0)
				{
					direction *= -1;
					setAhead(150 * direction);
				}
			}
			if (getEnergy() > 20 && getGunHeat() == 0)
			{
				if (e.getVelocity() == 0)
					setFire(3);
				else
					setFire(power);
			}
			execute();
		}
		scan();
	}
	
	public double angleFinder (double x, double y, double t) {
		double arcSin = Math.toDegrees(Math.asin(x / t));
		double bearing = 0;
		if (x > 0 && y > 0)
			bearing = arcSin;
		else if (x < 0 && y > 0)
			bearing = 360 + arcSin;
		else if (x > 0 && y < 0)
			bearing = 180 - arcSin;
		else if (x < 0 && y < 0)
			bearing = 180 - arcSin;
		else if (x == 0)
			if (y < 0)
				bearing = 180;
			else
				bearing = 0;
		else if (y == 0)
			if (x < 0)
				bearing = 270;
			else
				bearing = 90;
		
		return bearing;
	}
	
	public double normToAbs (double d) {
		if (d < 0)
			return (360 + d);
		else
			return d;
	}
	
	public double normalizeBearing(double angle) {
		while (angle >=  180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}
	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		setTurnRight(e.getBearing() - getHeading() + 90);
		ahead(150);
	}

	public void onHitRobot (HitRobotEvent e)
	{
		setTurnRight(e.getBearing());
		back(5);
		setAhead(50);
		setTurnGunRight(e.getBearing() + getHeading() - getGunHeading());
		fire(3);
	}
	
	public void onRobotDeath (RobotDeathEvent e) {
		if (getOthers() < 5)
		{
			wall = false;
			setAdjustGunForRobotTurn(true);
			ahead(50);
		}
	}
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		if (!wall)
		{
			setTurnRight(normalizeBearing(e.getBearing() + 180));
			setAhead(100);
		}
		else
		{
			turnRight(e.getBearing());
		}
	}	
}
