//import com.typesafe.config.{Config, ConfigFactory};
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import evaluate_example_datacenters.ExampleDataCentersEval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static Config conf = ConfigFactory.load("lightbend.conf");
    private int HOSTS = conf.getInt("conf.HOSTS");
    private int HOST_PES = conf.getInt("conf.HOST_PES");
    private int VMS = conf.getInt("conf.VMS");
    private int VM_PES = conf.getInt("conf.VM_PES");
    private int CLOUDLETS = conf.getInt("conf.CLOUDLETS");
    private int CLOUDLET_PES = conf.getInt("conf.CLOUDLET_PES");
    private int CLOUDLET_LENGTH = conf.getInt("conf.CLOUDLET_LENGTH");

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String args[]){new Main(); }

    private Main(){
        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    private Datacenter createDatacenter(){
        final List<Host> hostList = new ArrayList<>(HOSTS);

        for(int i=0;i<HOSTS; i++){
            Host host = createHost();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation,hostList);
    }

    private Host createHost(){
        final List<Pe> peList = new ArrayList<>(HOST_PES);

        for(int i=0;i<HOST_PES;i++){
            peList.add(new PeSimple(1000) );
        }
        final long ram = 2048;
        final long bw = 10000;
        final long storage = 1000000;

        return new HostSimple(ram, bw, storage, peList);
    }

    private List<Vm> createVms(){
        final List<Vm> list = new ArrayList<>(VMS);

        for(int i=0; i<VMS;i++){
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            list.add(vm);
        }
        return list;
    }

    private List<Cloudlet> createCloudlets(){
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

//        LOGGER.info("Testing if logger works");
//
//        LOGGER.debug(conf.getString("conf.name"));
//        for(int i=0;i<10;i++){
//            LOGGER.warn("Counter is {}", i);
//        }
//
//        System.out.print("Hello World");

}
