package edu.ncssm.ftc.electric_mayhem.i2c.vl53l1x

import android.graphics.Point
import android.util.Log
import org.firstinspires.ftc.robotcore.internal.opengl.models.Geometry
import org.opencv.core.Point3
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * This describes the region of interest (ROI) for the VL53L1X sensor.  The coordinates
 * are in terms of the SPAD array, which is a 16x16 grid of pixels.  The upper left corner
 * is (0,0) and the lower right corner is (15,15).  The ROI must be at least 4x4 pixels.
 *
 *  https://www.st.com/resource/en/user_manual/um2555-vl53l1x-ultra-lite-driver-multiple-zone-implementation-stmicroelectronics.pdf
 *
 *  @param X The X coordinate of the upper left corner of the ROI
 *  @param Y The Y coordinate of the upper left corner of the ROI
 *  @param spadWidth The width of the ROI in SPADs (minimum 4)
 *  @param spadHeight The height of the ROI in SPADs (minimum 4)
 */
data class ROI(val X : Int, val Y : Int, val spadWidth: Int, val spadHeight: Int, val name : String = "ROI") {

    companion object {
        val MINIMUM_SPAD_WIDTH = 4
        val MINIMUM_SPAD_HEIGHT = 4
        val VL53L1X_SPAD_CENTER_MATRIX = arrayOf(
            intArrayOf(128, 136, 144, 152, 160, 168, 176, 184, 192, 200, 208, 216, 224, 232, 240, 248),
            intArrayOf(129, 137, 145, 153, 161, 169, 177, 185, 193, 201, 209, 217, 225, 233, 241, 249),
            intArrayOf(130, 138, 146, 154, 162, 170, 178, 186, 194, 202, 210, 218, 226, 234, 242, 250),
            intArrayOf(131, 139, 147, 155, 163, 171, 179, 187, 195, 203, 211, 219, 227, 235, 243, 251),
            intArrayOf(132, 140, 148, 156, 164, 172, 180, 188, 196, 204, 212, 220, 228, 236, 244, 252),
            intArrayOf(133, 141, 149, 157, 165, 173, 181, 189, 197, 205, 213, 221, 229, 237, 245, 253),
            intArrayOf(134, 142, 150, 158, 166, 174, 182, 190, 198, 206, 214, 222, 230, 238, 246, 254),
            intArrayOf(135, 143, 151, 159, 167, 175, 183, 191, 199, 207, 215, 223, 231, 239, 247, 255),
            intArrayOf(127, 119, 111, 103, 95, 87, 79, 71, 63, 55, 47, 39, 31, 23, 15, 7),
            intArrayOf(126, 118, 110, 102, 94, 86, 78, 70, 62, 54, 46, 38, 30, 22, 14, 6),
            intArrayOf(125, 117, 109, 101, 93, 85, 77, 69, 61, 53, 45, 37, 29, 21, 13, 5),
            intArrayOf(124, 116, 108, 100, 92, 84, 76, 68, 60, 52, 44, 36, 28, 20, 12, 4),
            intArrayOf(123, 115, 107, 99, 91, 83, 75, 67, 59, 51, 43, 35, 27, 19, 11, 3),
            intArrayOf(122, 114, 106, 98, 90, 82, 74, 66, 58, 50, 42, 34, 26, 18, 10, 2),
            intArrayOf(121, 113, 105, 97, 89, 81, 73, 65, 57, 49, 41, 33, 25, 17, 9, 1),
            intArrayOf(120, 112, 104, 96, 88, 80, 72, 64, 56, 48, 40, 32, 24, 16, 8, 0)
        )
        fun createFromRegisterValues(spadWidth : Int, spadHeight : Int, spadCenterValue : Int) : ROI {
            // find the spad center value in the matrix, then lowerLeftX and lowerLeftY are the coordinates of the upper left corner of the ROI
            // remember that the spad array is 16x16 and
            var found = false
            val spadCenter = Point(0,0)
            for (i in 0..15) {
                for (j in 0..15) {
                    if (VL53L1X_SPAD_CENTER_MATRIX[i][j] == spadCenterValue) {
                        spadCenter.x = i
                        spadCenter.y = j
                        found = true
                        break
                    }
                }
            }
            if (!found) throw IllegalArgumentException("Could not find spad center value in the matrix")

            val X = if(spadWidth % 2 == 0) spadCenter.x - spadWidth / 2 else spadCenter.x - (spadWidth + 1) / 2
            val Y = if(spadHeight % 2 == 0) spadCenter.y - spadHeight / 2 else spadCenter.y - (spadHeight - 1) / 2
            return ROI(X, Y, spadWidth, spadHeight, "ROI from register values")
        }
        /**
         * Calculates the center point of the region of interest (ROI) in the SPAD (Single Photon Avalanche Diode) array.
         * The center point is determined based on the size of the ROI:
         * If the ROI width is even, the center is the rightmost of the center two pixels.
         * If the ROI height is even, the center is the topmost of the center two pixels.
         * If the ROI width is odd, the center is the center pixel.
         * If the ROI height is odd, the center is the center pixel.
         *
         * @param X The x-coordinate of the lower-left corner of the ROI.
         * @param Y The y-coordinate of the lower-left corner of the ROI.
         * @param spadWidth The width of the ROI in SPAD pixels.
         * @param spadHeight The height of the ROI in SPAD pixels.
         * @return A Point representing the center of the ROI.
         */
        private fun getSPADROICenter( X : Int,  Y : Int, spadWidth: Int, spadHeight: Int) : Point {
            // if the ROI is an even number of pixels wide, the center is the rightmost (higher x value) pixel of the center two
            // if the ROI is an even number of pixels tall, the center is the topmost (lower y value) pixel of the center two
            // if the ROI is an odd number of pixels wide, the center is the center pixel
            // if the ROI is an odd number of pixels tall, the center is the center pixel
            val x = if(spadWidth % 2 == 0) X + spadWidth / 2 else X + (spadWidth + 1) / 2
            val y = if(spadHeight % 2 == 0) Y + spadHeight / 2 - 1 else Y + (spadHeight - 1) / 2
            return Point(x, y)
        }
        /**
         * Retrieves the value from the SPAD center matrix at the given ROI center point.
         *
         * @param spadROICenter A Point representing the center of the ROI.
         * @return The value from the SPAD center matrix at the given point.
         */
        private fun getSPADROICenterValue(spadROICenter : Point) : Int {
            return VL53L1X_SPAD_CENTER_MATRIX[spadROICenter.y][spadROICenter.x]
        }
        /**
         * Calculates a unit vector representing the direction of the ROI from the center of the SPAD array.
         * The direction is calculated based on the field of view (FoV) of each SPAD and the deviation of the ROI center from the center of the array.
         *
         * @param spadROICenter A Point representing the center of the ROI.
         * @return A Point3 representing the direction of the ROI as a unit vector.
         */
        private fun getDirection(spadROICenter : Point) : Point3 {
            // given that the spad array is 16x16 and the diagonal field of view (FoV) is 27 degrees, the FoV of each spad is 27 / sqrt(2) / 16 degrees
            // So as we move from the center of the ROI to the edge, the angle changes by 27 / sqrt(2) / 16 degrees per spad
            // from this, we can calculate a unit vector in the direction of the ROI from the center of the array
            val diagonalFoV = Math.toRadians(27.0)
            val xyFoV = diagonalFoV / sqrt(2.0)
            val xyFoVperSpad = xyFoV / 16.0

            val defaultCenter = Point(8,7)
            // calculate deviation from the center
            val deltaX = (spadROICenter.x - defaultCenter.x) * xyFoVperSpad
            val deltaY = (spadROICenter.y - defaultCenter.y) * xyFoVperSpad

            // calculate components of the direction vector
            val x = -sin(deltaX) // left/right deviation
            val y = -sin(deltaY) // up/down deviation
            val z = sqrt(1.0 - x*x - y*y) // forward direction, calculated so the length of the vector is 1

            return Point3(x, y, z)
        }
    }
    // this is taken from the application note https://www.st.com/resource/en/user_manual/um2555-vl53l1x-ultra-lite-driver-multiple-zone-implementation-stmicroelectronics.pdf
    val spadROICenter = getSPADROICenter(X, Y, spadWidth, spadHeight)
    val spadROICenterValue = getSPADROICenterValue(spadROICenter)
    val direction = getDirection(spadROICenter)
    init {
        if(spadHeight < MINIMUM_SPAD_HEIGHT || spadWidth < MINIMUM_SPAD_WIDTH) throw java.lang.IllegalArgumentException("ROI size must be at least 4x4 spads, spadWidth = $spadWidth, spadHeight = $spadHeight")
        if(X < 0 || X > (16 - MINIMUM_SPAD_WIDTH ))
            throw IllegalArgumentException("X must be between 0 and ${16 - MINIMUM_SPAD_WIDTH}, X = $X")
        if(Y < 0 || Y > (16 - MINIMUM_SPAD_HEIGHT))
            throw IllegalArgumentException("Y must be between 0 and ${16 - MINIMUM_SPAD_HEIGHT}, Y = $Y")

        Log.i("VL53L1X", "ROI created ($name) with X = $X, Y = $Y, spadWidth = $spadWidth, spadHeight = $spadHeight")
        Log.i("VL53L1X", "direction = $direction")
        Log.i("VL53L1X", "spadROICenter = $spadROICenter")
        Log.i("VL53L1X", "spadROICenterValue = $spadROICenterValue")
    }

}
