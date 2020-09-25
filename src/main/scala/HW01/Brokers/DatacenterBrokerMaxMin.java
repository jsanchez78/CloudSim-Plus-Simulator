package HW01.Brokers;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A implementation of  that uses a Best Fit
 * mapping between submitted cloudlets and Vm's, trying to place a Cloudlet
 * at the best suitable Vm which can be found (according to the required Cloudlet's PEs).
 * The Broker then places the submitted Vm's at the first Datacenter found.
 * If there isn't capacity in that one, it will try the other ones.
 *
 * @author Humaira Abdul Salam
 * @since CloudSim Plus 4.3.8
 */
public class DatacenterBrokerMaxMin extends DatacenterBrokerSimple {

    /**
     * Creates a DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerMaxMin(final CloudSim simulation) {
        super(simulation);
    }

    @Override
    public void requestDatacentersToCreateWaitingCloudlets(){
        // Sort Cloudlets in Queue
        List<Cloudlet> queue = mapCloudlets(getCloudletWaitingList(),getVmCreatedList());
        getCloudletWaitingList().clear();
        // Replace the Queue w/ Cloudlets (Least Execution Time to Most)
        getCloudletWaitingList().addAll(queue);
        super.requestDatacentersToCreateWaitingCloudlets();
    }
    private List<Cloudlet> mapCloudlets(List<Cloudlet> cloudletList, List<Vm> VM_list){
        Map<Cloudlet, Double> mapped_cloudlets = new HashMap<>();
        for(Cloudlet c: cloudletList){
            /* TODO: */
            mapped_cloudlets.put(c, getMinTimeToExecuteCloudlet(c,VM_list));
        }
        // SORT Map w/ value in ASC Order
        Map<Cloudlet, Double> sorted_map = mapped_cloudlets.entrySet()
                        .stream()
                .sorted((Map.Entry.<Cloudlet, Double>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<Cloudlet> sorted_cloudlets = sorted_map.keySet().stream()
                .collect(Collectors.toList());
        return sorted_cloudlets;
    }
    private double getMinTimeToExecuteCloudlet(Cloudlet c, List<Vm> VM_list){
        double minExecutionTime = 0;
        // Get VMs so cloudlets cannot be exceeded
        for(Vm v: VM_list){
            if (v.isSuitableForCloudlet(c))
                minExecutionTime = Math.min(minExecutionTime, c.getTotalLength() / (v.getTotalMipsCapacity() - v.getTotalCpuMipsUsage()));
        }
        // Get Execution time
        return minExecutionTime;
    }
}
