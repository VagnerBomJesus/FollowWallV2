import com.ridgesoft.intellibrain.IntelliBrain;
import com.ridgesoft.io.Display;
import com.ridgesoft.robotics.Motor;
import com.ridgesoft.robotics.ContinuousRotationServo;
import com.ridgesoft.robotics.SonarRangeFinder;
import com.ridgesoft.robotics.sensors.ParallaxPing; 

public class FollowWallV2 {
		
	private static Display lcd; 
	private static Motor leftMotor;
	private static Motor rightMotor;
	private static SonarRangeFinder rightSonar;
	private static SonarRangeFinder frontSonar;
	private static float rightDist;
	private static float frontDist;
	private static final int BASE_POWER = 8;
	private static final int DIST_TO_SIDE_WALL = 15; 
	private static final int DIST_TO_FRONT_WALL = 15;
	private static final float GAIN = 0.8f;


	public static void main(String[] args) {
		
		lcd = IntelliBrain.getLcdDisplay();
		leftMotor = new ContinuousRotationServo(IntelliBrain.getServo(1), false, 14);
		rightMotor = new ContinuousRotationServo(IntelliBrain.getServo(2), true, 14);
		rightSonar = new ParallaxPing(IntelliBrain.getDigitalIO(5));
		frontSonar = new ParallaxPing(IntelliBrain.getDigitalIO(4));
		
		try {
			
			lcd.print(0, "Follow Wall 1");
			
			while (true) {
				rightSonar.ping();
				frontSonar.ping();
				Thread.sleep(50);
				rightDist = rightSonar.getDistanceCm();
				frontDist = frontSonar.getDistanceCm();
				followRightWallV2(); 
				rotateWall();
				
			} 
			
		}
		catch (Throwable t) {
		t.printStackTrace();
		} 
		 
	}
	
	private static void move(int power, int offset) {
		 leftMotor.setPower(power + offset);
		 rightMotor.setPower(power - offset);
		} 
	
	private static void followRightWallV1() {
		
		 if (rightDist <= 0)
			 rightDist = 100.0f;
		 if (rightDist == DIST_TO_SIDE_WALL)
		 move(BASE_POWER, 0); // Advance straight
		 else if (rightDist > DIST_TO_SIDE_WALL)
		 move(BASE_POWER, 3); // Advance curving right
		 else
		 move(BASE_POWER, -3); // Advance curving left
		} 
	
	private static void rotateWall() {
		if (frontDist <= 20.0f) 
			rotate90();
		
		if (frontDist <= 0)
			
			frontDist = 100.0f;
		
		
	}
	
	private static void followRightWallV2() {
		
		if (rightDist <= 0)
			rightDist = 100.0f;
		
		 float error = rightDist - DIST_TO_SIDE_WALL;
			
		 
		 int delta = (int) (error * GAIN);
		 
		 if (delta > 6)
			 delta = 6;
		 else if (delta < -6)
			 delta = -6;

				 
		 move(BASE_POWER, delta); 
		}

	public static void wait (int millis) {
		 
		 try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		 }
	
	public static void rotate90() {
		 leftMotor.setPower(-8);
		 rightMotor.setPower(8);
		 wait(350);
		 leftMotor.brake();
		 rightMotor.brake();
		} 
	

	
	private static void displayDist() {
		 if (rightDist > 0.0f)
		 lcd.print(1, "R: " + (int) (rightDist + 0.5f) + " Cm");
		 else
		 lcd.print(1, "--");
		 
		 if (frontDist > 0.0f)
			 lcd.print(1, "R: " + (int) (frontDist + 0.5f) + " Cm");
			 else
			 lcd.print(1, "--");
		} 
}

