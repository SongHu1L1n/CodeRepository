/*
 * Title:        EdgeCloudSim - CloudVM
 * 
 * Description: 
 * CloudVM adds vm type information over CloudSim's VM class
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.cloud_server;

import edu.boun.edgecloudsim.core.SimSettings;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.List;

public class CloudVM extends Vm {
	private SimSettings.VM_TYPES type;

	public CloudVM(int id, int userId, double mips, int numberOfPes, int ram,
			long bw, long size, String vmm, CloudletScheduler cloudletScheduler) {
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);

		type = SimSettings.VM_TYPES.CLOUD_VM;
	}

	public SimSettings.VM_TYPES getVmType(){
		return type;
	}

	/**
	 *  dynamically reconfigures the mips value of a  VM in CloudSim 动态重新配置CloudSim中VM的mips值
	 * 
	 * @param mips new mips value for this VM.
	 */
	public void reconfigureMips(double mips){
		super.setMips(mips);
		super.getHost().getVmScheduler().deallocatePesForVm(this);

		List<Double> mipsShareAllocated = new ArrayList<Double>();
		for(int i = 0; i < getNumberOfPes(); i++)
			mipsShareAllocated.add(mips);

		super.getHost().getVmScheduler().allocatePesForVm(this, mipsShareAllocated);
	}
}
