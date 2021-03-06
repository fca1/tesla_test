/*
 * BasicTest.java - Copyright(c) 2013 Joe Pasqua
 * Provided under the MIT License. See the LICENSE file for details.
 * Created: Jul 5, 2013
 */

package org.noroomattheinn.tesla.test;

import java.util.List;
import org.noroomattheinn.tesla.ChargeState;
import org.noroomattheinn.tesla.HVACState;
import org.noroomattheinn.tesla.Tesla;
import org.noroomattheinn.tesla.Vehicle;
//import org.noroomattheinn.utils.Utils;

/**
 * BasicTest
 *
 * @author Joe Pasqua <joe at NoRoomAtTheInn dot org>
 */

public class HornTest {
	Vehicle	currentVehicle;
	
	public HornTest(Vehicle	currentVehicle)
	{
	this.currentVehicle = currentVehicle;	
	}
	
	public boolean isEnergyPowerIsSufficient(double seuil)
	{
    	ChargeState cst  = currentVehicle.queryCharge();
    	double percentBat = cst.batteryPercent;
    	boolean autorizedToHeat = !cst.notEnoughPowerToHeat;
    	return autorizedToHeat && (percentBat>seuil);
	}
	
	public double getTemperatureCOutside() throws Exception
	{
    HVACState hvac = currentVehicle.queryHVAC();
	double outSideTemp = hvac.outsideTemp;
    if (hvac.valid)
    	{
    	return outSideTemp;
    	}
    throw new Exception("Impossible de lire la temperature");
	}
	
	
	public double getTemperatureCInside() throws Exception
		{
        HVACState hvac = currentVehicle.queryHVAC();
        if (hvac.valid)
        	{
        	
        	double t = hvac.insideTemp;
        	if (Double.isNaN(t))
        		{
        		currentVehicle.startAC();
        		currentVehicle.stopAC();
        		return getTemperatureCInside();
        		}
        	return t;
        	}
        throw new Exception("Impossible de lire la temperature");
		}
	
	
	public void enableAc(boolean enable, double temp)
	{
		
		if (enable)
			{
			currentVehicle.setTempC(temp, temp);
			currentVehicle.startAC();
			}
		else
			{
			currentVehicle.stopAC();
			}
	
	}

     /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {            
        Tesla t = new Tesla();
                
        if (args.length == 2) {
            if (!t.connect(args[0], args[1])) {
                System.err.println("Unable to connect with supplied u/p");
                System.exit(1);
            }
        } else if (args.length == 3) {  // -t username token
            if (!t.connectWithToken(args[0], args[1])) {
                System.err.println("Unable to connect with stored credentials");
                System.exit(1);
            }
        }
        
        
        List<Vehicle> vehicles = t.getVehicles();
        
        HornTest h= new HornTest(vehicles.get(0));
        double temp = h.getTemperatureCInside();
        if (temp<16.0 && h.isEnergyPowerIsSufficient(20.0))
        	{
        	h.enableAc(true, 20.0);
        	h.getTemperatureCOutside();
        	}
        
    }
   
}
