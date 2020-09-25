package HW01.Simulation;

import HW01.Allocation.Policies.VMAllocation.VmAllocationPolicyRoundRobin;
import HW01.Brokers.DatacenterBrokerMaxMin;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import scala.Int;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BasicFirstExample {
    
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;


    private final CloudSim simulation1;
    private DatacenterBroker broker1;
    private List<Vm> vmList1;
    private List<Cloudlet> cloudletList1;
    private Datacenter datacenter1;





    static Integer HOSTS;
    static Integer HOST_PES;
    static Integer HOST_ram;
    final Integer HOST_bw;
    static Integer HOST_MIPS;

    static Integer VMS;
    static Integer VM_PES;
    static Integer VM_MIPS;
    static Integer VM_STORAGE;
    static Integer ram;
    final Integer bw;
    final Integer storage;
    
    static Integer CLOUDLETS;
    static Integer CLOUDLET_PES;
    static Integer CLOUDLET_LENGTH;
    static Integer CLOUDLET_SET_SIZE;
    static List<Integer> CLOUDLET_dynamic;


    
    
    public static void main(String[] args) {
        new BasicFirstExample();
    }

    private BasicFirstExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        Config config = ConfigFactory.load("inputs.conf");
        HOSTS = Integer.parseInt(config.getString("jdbc.HOSTS"));
        HOST_PES = Integer.parseInt(config.getString("jdbc.HOST_PES"));
        HOST_ram = Integer.parseInt(config.getString("jdbc.HOST_ram"));
        HOST_bw = Integer.parseInt(config.getString("jdbc.HOST_bw"));
        HOST_MIPS = Integer.parseInt(config.getString("jdbc.HOST_MIPS"));

        VMS = Integer.parseInt(config.getString("jdbc.VMS"));
        VM_PES = Integer.parseInt(config.getString("jdbc.VM_PES"));
        bw = Integer.parseInt(config.getString("jdbc.bw"));; //in Megabits/s
        ram = Integer.parseInt(config.getString("jdbc.ram")); //in Megabytes
        VM_MIPS = Integer.parseInt(config.getString("jdbc.VM_MIPS"));
        VM_STORAGE = Integer.parseInt(config.getString("jdbc.VM_STORAGE"));

        CLOUDLETS = Integer.parseInt(config.getString("jdbc.CLOUDLETS"));
        CLOUDLET_PES = Integer.parseInt(config.getString("jdbc.CLOUDLET_PES"));
        CLOUDLET_LENGTH = Integer.parseInt(config.getString("jdbc.CLOUDLET_LENGTH"));
        storage = Integer.parseInt(config.getString("jdbc.storage"));


        CLOUDLET_SET_SIZE = VM_STORAGE = Integer.parseInt(config.getString("jdbc.CLOUDLET_SET_SIZE"));
        CLOUDLET_dynamic = config.getIntList("jdbc.CLOUDLET_dynamic");


        simulation = new CloudSim();
        simulation1 = new CloudSim();

        Datacenter datacenter0 = createDatacenter();
        Datacenter datacenter1 = createDatacenter1();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);
        broker1 = new DatacenterBrokerMaxMin(simulation1);
        broker0.setVmDestructionDelayFunction(vm -> 30.0);

        vmList = createVms();
        vmList1 = createVms();


        cloudletList = createCloudlets_dynamic(CLOUDLET_dynamic, CLOUDLET_dynamic.size());
        cloudletList1 = createCloudlets_dynamic(CLOUDLET_dynamic, CLOUDLET_dynamic.size());

        broker0.submitVmList(vmList);
        broker1.submitVmList(vmList1);

        broker0.submitCloudletList(cloudletList);
        broker1.submitCloudletList(cloudletList1);

        simulation.start();
        simulation1.start();


        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        final List<Cloudlet> finishedCloudlets1 = broker1.getCloudletFinishedList();

        new CloudletsTableBuilder(finishedCloudlets).build();
        new CloudletsTableBuilder(finishedCloudlets1).build();
    }
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList).setSchedulingInterval(5);
    }
    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter1() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        final VmAllocationPolicyRoundRobin allocationPolicy = new VmAllocationPolicyRoundRobin();
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation1, hostList, allocationPolicy).setSchedulingInterval(5);
    }
    /**
     * A method that defines a Best Fit policy to select a suitable Host with the least
     * available PEs to place a VM.
     * Using Java 8 Functional Programming, this method is given as parameter
     * to the constructor of a {@link VmAllocationPolicySimple}
     *
     * @param allocationPolicy the {@link VmAllocationPolicy} that is trying to allocate a Host for the requesting VM
     * @param vm the VM to find a host to
     * @return an {@link Optional <Host>} which may contain a Host or an empty Optional if no suitable Host was found
     * @see #createDatacenter()
     */
    private Optional<Host> bestFitHostSelectionPolicy(VmAllocationPolicy allocationPolicy, Vm vm) {
        return allocationPolicy
                .getHostList()
                .stream()
                .filter(host -> host.isSuitableForVm(vm))
                .min(Comparator.comparingInt(Host::getFreePesNumber));
    }
    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(HOST_MIPS));
        }
        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        Host host = new HostSimple(HOST_ram, HOST_bw, storage, peList);
        host
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }
    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(ram).setBw(bw).setSize(VM_STORAGE);
            list.add(vm);
        }
        return list;
    }
    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(CLOUDLET_SET_SIZE);
            list.add(cloudlet);
        }
        return list;
    }
    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets_dynamic(List<Integer> cloudletLengthList, int size) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        int cloudlet_length;
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudlet_length = (i < size) ? cloudletLengthList.get(i) : cloudletLengthList.get(i % size);
            final Cloudlet cloudlet = new CloudletSimple(cloudlet_length, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(CLOUDLET_SET_SIZE);
            list.add(cloudlet);
        }
        return list;
    }




}

