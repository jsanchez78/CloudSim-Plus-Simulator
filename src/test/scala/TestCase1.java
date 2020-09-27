import HW01.Simulation.BasicFirstExample;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestCase1 {
    private List<Cloudlet> cloudletList;
    BasicFirstExample simulation = new BasicFirstExample();
    @Test
    public void compare_cost_and_profit(){
        assertNotEquals(simulation.getCost(),simulation.getCostChargingCustomer());
    }
    @Test
    public void create_cloudlets(){
        simulation.run_simulations();
        assertNotNull(simulation.getCloudletList());
    }
    @Test
    public void distinct_data_centers(){
        simulation.run_simulations();
        assertNotEquals(simulation.getDatacenter0(),simulation.getDatacenter1());
    }
    @Test
    public void VMAllocationPolicyFirstFit(){
        simulation.run_simulations();
        assertEquals(simulation.getDatacenter0VMAllocationPolicy().getClass(), VmAllocationPolicyFirstFit.class);
    }
    @Test
    public void confirm_VMAllocationPolicyBestFit(){
        simulation.run_simulations();
        assertEquals(simulation.getDatacenter0VMAllocationPolicy().getClass(), VmAllocationPolicyFirstFit.class);
    }
    @Test
    public void VMAllocationPolicyBestFit(){

    }
    @AfterClass
    public static void simulation_non_empty(){
        BasicFirstExample simulation = new BasicFirstExample();
        assertNotNull(simulation);
    }

}
