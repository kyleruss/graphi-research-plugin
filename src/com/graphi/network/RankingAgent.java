//======================================================
//  Kyle Russell
//  AUT University 2015
//  https://github.com/denkers/graphi-research-plugin
//======================================================

package com.graphi.network;

import com.graphi.graph.Node;
import com.graphi.network.rank.TrendingSourceComparator;
import java.awt.Color;
import java.util.Comparator;
import java.util.PriorityQueue;

public class RankingAgent extends InfluenceAgent
{
    private RankingAgent treeRootAgent;
    private int propagationCount;
    private Comparator<Node> policyComparator;
    private PriorityQueue<InfluenceAgent> influenceOffers;
            
    public RankingAgent()
    {
        this(0, "", new TrendingSourceComparator());
    }
    
    public RankingAgent(int id, Comparator<Node> policyComparator)
    {
        this(id, "", policyComparator);
    }
    
    public RankingAgent(int id, String name, Comparator<Node> policyComparator)
    {
        this(id, name, null, policyComparator);
    }
    
    public RankingAgent(int id, String name, Color fill, Comparator<Node> policyComparator)
    {
        super(id, name, fill);
        
        propagationCount        =   0;
        treeRootAgent           =   null;
        this.policyComparator   =   policyComparator;
        influenceOffers         =   new PriorityQueue<>(policyComparator);   
    }
    
    public Node chooseOptimalInfluencer()
    {
        return influenceOffers.peek();
    }
    
    public void clearInfluenceOffers()
    {
        influenceOffers.clear();
    }
    
    @Override
    public void influenceAgent(InfluenceAgent target)
    {
        super.influenceAgent(target);
        
        propagationCount++;
        RankingAgent rankingTarget  =   (RankingAgent) target;
        rankingTarget.setTreeRootAgent(getTreeRootAgent());
    }
    
    public void addInfluenceOffer(RankingAgent influenceAgent)
    {
        influenceAgent.addInfluenceOffer(this);
    }
    
    public RankingAgent getTreeRootAgent() 
    {
        return treeRootAgent == null? this : treeRootAgent;
    }

    public void setTreeRootAgent(RankingAgent treeRootAgent) 
    {
        this.treeRootAgent = treeRootAgent;
    }

    public int getPropagationCount() 
    {
        return propagationCount;
    }

    public void setPropagationCount(int propagationCount)
    {
        this.propagationCount = propagationCount;
    }

    public Comparator<Node> getPolicyComparator()
    {
        return policyComparator;
    }

    public PriorityQueue<InfluenceAgent> getInfluenceOffers() 
    {
        return influenceOffers;
    }
    
    public int getInfluenceOfferCount()
    {
        return influenceOffers.size();
    }
    
    public boolean hasInfluenceOffers()
    {
        return !influenceOffers.isEmpty();
    }
}
