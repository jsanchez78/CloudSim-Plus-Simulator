package HW01.Simulation;

import HW01.Brokers.DatacenterBrokerMaxMin;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.listeners.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.DecimalFormat;
import java.util.*;

public class BasicFirstExample {
    private Logger logger;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private DatacenterCharacteristics datacenterCharacteristic0;

    private final CloudSim simulation1;
    private DatacenterBroker broker1;
    private List<Vm> vmList1;
    private List<Cloudlet> cloudletList1;
    private Datacenter datacenter1;
    private DatacenterCharacteristics datacenterCharacteristic1;

    private final CloudSim simulation2;
    private DatacenterBroker broker2;
    private List<Vm> vmList2;
    private List<Cloudlet> cloudletList2;
    private Datacenter datacenter2;
    private DatacenterCharacteristics datacenterCharacteristic2;

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

    // Cost
    static List<Integer> cost;
    static List<Double> cost_for_customer;
    public static void main(String[] args) {
        new BasicFirstExample();
    }
    /*
    *
    *   Simulations Run:
    *
    *   Set1:
    *       DynamicUtilization => 0.50
    *       VMAllocation Policy: RandomFit
    *
    *       DynamicUtilization => 0.99
    *       VMAllocation Policy: BestFit
    *
    *   Set2:
    *
    *   Set3:
    *
    *
    *
    *
    * */
    public BasicFirstExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        logger = LoggerFactory.getLogger(BasicFirstExample.class);

        Config config = ConfigFactory.load("inputs.conf");
        HOSTS = Integer.parseInt(config.getString("architecture0.HOSTS"));
        HOST_PES = Integer.parseInt(config.getString("architecture0.HOST_PES"));
        HOST_ram = Integer.parseInt(config.getString("architecture0.HOST_ram"));
        HOST_bw = Integer.parseInt(config.getString("architecture0.HOST_bw"));
        HOST_MIPS = Integer.parseInt(config.getString("architecture0.HOST_MIPS"));

        VMS = Integer.parseInt(config.getString("architecture0.VMS"));
        VM_PES = Integer.parseInt(config.getString("architecture0.VM_PES"));
        bw = Integer.parseInt(config.getString("architecture0.bw"));; //in Megabits/s
        ram = Integer.parseInt(config.getString("architecture0.ram")); //in Megabytes
        VM_MIPS = Integer.parseInt(config.getString("architecture0.VM_MIPS"));
        VM_STORAGE = Integer.parseInt(config.getString("architecture0.VM_STORAGE"));

        CLOUDLETS = Integer.parseInt(config.getString("architecture0.CLOUDLETS"));
        CLOUDLET_PES = Integer.parseInt(config.getString("architecture0.CLOUDLET_PES"));
        CLOUDLET_LENGTH = Integer.parseInt(config.getString("architecture0.CLOUDLET_LENGTH"));
        storage = Integer.parseInt(config.getString("architecture0.storage"));

        CLOUDLET_SET_SIZE = VM_STORAGE = Integer.parseInt(config.getString("architecture0.CLOUDLET_SET_SIZE"));
        CLOUDLET_dynamic = config.getIntList("architecture0.CLOUDLET_dynamic");

        cost = config.getIntList("architecture0.cost");
        cost_for_customer = config.getDoubleList("architecture0.cost_for_customer");

        simulation = new CloudSim();
        simulation1 = new CloudSim();
        simulation2 = new CloudSim();
    }
    public void run_simulations(){
        datacenter0 = createDatacenter();
        datacenter1 = createDatacenter1();
        datacenter2 = createDatacenter2();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);
        broker1 = new DatacenterBrokerSimple(simulation1);
        broker2 = new DatacenterBrokerMaxMin(simulation2);

        logger.info("LOGGING INFO: Submitting Vms");
        vmList = createAndSubmitVms(broker0);
        vmList1 = createAndSubmitVms(broker1);
        vmList2 = createAndSubmitVms(broker2);

        logger.info("LOGGING INFO: Dynamically Create cloudlet list");
        cloudletList = createCloudlets_dynamic(CLOUDLET_dynamic, CLOUDLET_dynamic.size());
        cloudletList1 = createCloudlets_dynamic2(CLOUDLET_dynamic, CLOUDLET_dynamic.size());
        cloudletList2 = createCloudlets_dynamic3(CLOUDLET_dynamic, CLOUDLET_dynamic.size());

        broker0.submitCloudletList(cloudletList);
        broker1.submitCloudletList(cloudletList1);
        broker2.submitCloudletList(cloudletList2);

        simulation.start();
        simulation1.start();
        simulation2.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        final List<Cloudlet> finishedCloudlets1 = broker1.getCloudletFinishedList();
        final List<Cloudlet> finishedCloudlets2 = broker2.getCloudletFinishedList();

        print_cost_statistics(finishedCloudlets);
        print_cost_statistics(finishedCloudlets1);
        print_cost_statistics(finishedCloudlets2);

        statistics_per_cloudlet(finishedCloudlets);
        statistics_per_cloudlet(finishedCloudlets1);
    }
    private void print_cost_statistics(List<Cloudlet> cloudletList){
        new CloudletsTableBuilder(cloudletList).setTitle("Simulation Results: Broker0")
                .addColumn(new TextTableColumn("CPU Cost", "USD"), cloudlet -> new DecimalFormat("#.000").format(cloudlet.getActualCpuTime() * cloudlet.getCostPerSec()))
                .addColumn(new TextTableColumn("Bandwidth Cost", "USD"), Cloudlet::getAccumulatedBwCost)
                .addColumn(new TextTableColumn("Total Cost", "USD"), cloudlet -> new DecimalFormat("#.000").format(cloudlet.getTotalCost()))
                .build();
    }
    private void statistics_per_cloudlet(List<Cloudlet> cloudletList){
        long AVG_cloudlet_length = cloudletList.stream().map(Cloudlet::getTotalLength).reduce(0L, Long::sum) / cloudletList.size();
        double AVG_total_execution_time = cloudletList.stream().map(cloudlet -> cloudlet.getFinishTime() - cloudlet.getExecStartTime()).reduce(0.00, Double::sum);
        double AVG_cost = cloudletList.stream().map(Cloudlet::getTotalCost).reduce(0.00, Double::sum);
        double AVG_CPU_COST = cloudletList.stream().map(cloudlet -> cloudlet.getActualCpuTime() * cloudlet.getCostPerSec()).reduce(0.00, Double::sum);
        double AVG_bw_cost = cloudletList.stream().map(Cloudlet::getCostPerBw).reduce(0.00, Double::sum);
        double total_profit = AVG_cost * cost_for_customer.get(0);

        System.out.println(
                "\nAVG Execution Total Execution Time: " + AVG_total_execution_time + " seconds" +
                "\nAVG Cpu Cost: " + "$ " + AVG_CPU_COST +
                "\nAVG Bandwidth Cost: " + "$ " + AVG_bw_cost +
                "\nAVG Total Cost " + "$ " + AVG_cost +
                "\nAVG Cloudlet length: " + AVG_cloudlet_length +
                "\nProfit: " + "$ " + total_profit);
    }
    private void onClockTickListener(EventInfo evt) {
        vmList.forEach(vm ->
                System.out.printf(
                        "\t\tTime %6.1f: Vm %d CPU Usage: %6.2f%% (%2d vCPUs. Running Cloudlets: #%d). RAM usage: %.2f%% (%d MB)%n",
                        evt.getTime(), vm.getId(),
                        vm.getCloudletScheduler().getCloudletExecList().size(),
                        vm.getRam().getPercentUtilization()*100, vm.getRam().getAllocatedResource())
        );
    }
    private List<Vm> createAndSubmitVms(DatacenterBroker broker) {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm =
                    new VmSimple(VM_MIPS, VM_PES)
                            .setRam(ram).setBw(bw).setSize(VM_STORAGE)
                            .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
            list.add(vm);
        }
        broker.submitVmList(list);
        return list;
    }

    private List<Cloudlet> createAndSubmitCloudlets(DatacenterBroker broker) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        for (int i = 1; i <= CLOUDLETS; i++) {
            final UtilizationModel utilization = new UtilizationModelFull();
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
            cloudlet
                    .setFileSize(1024*i)
                    .setOutputSize(1024)
                    .setUtilizationModel(utilization);
            list.add(cloudlet);
        }

        broker.submitCloudletList(list);

        return list;
    }
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        final VmAllocationPolicyFirstFit allocationPolicy = new VmAllocationPolicyFirstFit();
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList, allocationPolicy).setSchedulingInterval(10).getCharacteristics()
                .setCostPerSecond(cost.get(0))
                .setCostPerMem(cost.get(1))
                .setCostPerStorage(cost.get(2))
                .setCostPerBw(cost.get(3))
                .getDatacenter();
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
        final VmAllocationPolicyBestFit allocationPolicy = new VmAllocationPolicyBestFit(this::bestFitHostSelectionPolicy);
        //Uses a VmAllocationPolicySimple by default to allocate VMs
       return new DatacenterSimple(simulation, hostList, allocationPolicy).setSchedulingInterval(10)
               .getCharacteristics().setCostPerSecond(cost.get(0))
                .setCostPerMem(cost.get(1))
                .setCostPerStorage(cost.get(2))
                .setCostPerBw(cost.get(3))
                .getDatacenter();
    }
    private Datacenter createDatacenter2() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        final VmAllocationPolicyBestFit allocationPolicy = new VmAllocationPolicyBestFit(this::bestFitHostSelectionPolicy);
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation1, hostList, allocationPolicy).setSchedulingInterval(5)
                .getCharacteristics().setCostPerSecond(cost.get(0))
                .setCostPerMem(cost.get(1))
                .setCostPerStorage(cost.get(2))
                .setCostPerBw(cost.get(3))
                .getDatacenter();
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
        /* TODO: Dynamic by executionTime */

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
    private List<Cloudlet> createCloudlets_dynamic2(List<Integer> cloudletLengthList, int size) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        int cloudlet_length;
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.99);
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudlet_length = (i < size) ? cloudletLengthList.get(i) : cloudletLengthList.get(i % size);
            final Cloudlet cloudlet = new CloudletSimple(cloudlet_length, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(CLOUDLET_SET_SIZE);
            list.add(cloudlet);
        }
        return list;
    }
    private List<Cloudlet> createCloudlets_dynamic3(List<Integer> cloudletLengthList, int size) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        int cloudlet_length;
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudlet_length = (i < size) ? cloudletLengthList.get(i) : cloudletLengthList.get(i % size);
            final Cloudlet cloudlet = new CloudletSimple(cloudlet_length, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(CLOUDLET_SET_SIZE);
            list.add(cloudlet);
        }
        return list;
    }
    /* Getters */
    public List<Cloudlet> getCloudletList(){
        return this.cloudletList;
    }
    public Datacenter getDatacenter0(){
        return this.datacenter0;
    }
    public Datacenter getDatacenter1(){
        return this.datacenter1;
    }
    public List<Integer> getCost(){
        return this.cost;
    }
    public List<Double> getCostChargingCustomer(){
        return this.cost_for_customer;
    }
    public VmAllocationPolicy getDatacenter0VMAllocationPolicy(){
        return this.datacenter0.getVmAllocationPolicy();
    }
    public VmAllocationPolicy getDatacenter1VMAllocationPolicy(){
        return this.datacenter1.getVmAllocationPolicy();
    }
}