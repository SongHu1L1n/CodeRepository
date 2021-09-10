/*
 * Title:        EdgeCloudSim - Edge Orchestrator
 * 
 * Description: 
 * EdgeOrchestrator is an abstract class which is used for selecting VM
 * for each client requests. For those who wants to add a custom 
 * Edge Orchestrator to EdgeCloudSim should extend this class and provide
 * a concrete instance via ScenarioFactory
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */
// EdgeOrchestrator是一个抽象类，用于为每个客户端请求选择VM。对于那些想向EdgeCloudSim添加自定义边编排程序的人，应该扩展该类，并通过ScenarioFactory提供具体实例
package edu.boun.edgecloudsim.edge_orchestrator;
import edu.boun.edgecloudsim.edge_client.Task;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.SimEntity;

public abstract class EdgeOrchestrator extends SimEntity{
	protected String policy;
	protected String simScenario;
//	传入卸载策略和场景
	public EdgeOrchestrator(String _policy, String _simScenario){
		super("EdgeOrchestrator");
		policy = _policy;
		simScenario = _simScenario;
	}

//  Default Constructor: Creates an empty EdgeOrchestrator
	// 默认构造函数：创建一个空的EdgeOrchestrator
	public EdgeOrchestrator() {
        	super("EdgeOrchestrator");
	}


//	initialize edge orchestrator if needed
	public abstract void initialize();
	
//	 * decides where to offload
	public abstract int getDeviceToOffload(Task task);
	
	/*
	 * returns proper VM from the edge orchestrator point of view
	 */
//	获取卸载的边缘服务器虚拟机
	public abstract Vm getVmToOffload(Task task, int deviceId);
}
