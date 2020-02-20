package com.pp.proxied.utilities.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.pp.proxied.utilities.schema.MoneyInteger;

public class Percentages
{
	public static void adjustWeightedSumToTarget(int iTarget, List<PaymentDetails> lDetails)
	{
		if ((null == lDetails) || (0 == lDetails.size()))
		{
			return;
		}
		int[] arValues = new int[lDetails.size()];
		int idx = 0;
		for (PaymentDetails current : lDetails)
		{
			arValues[idx++] = current.getAmount();
		}
		int[] arExtraPaidBeforeProcessing = new int[lDetails.size()];
		idx = 0;
		for (PaymentDetails current : lDetails)
		{
			arExtraPaidBeforeProcessing[idx++] = current.getExtraPaidBeforeProcessing().getAmount();
		}
		int[] arExtraPaidDuringProcessing = adjustWeightedSumToTarget(iTarget, arValues, arExtraPaidBeforeProcessing);
		
		for (int i=0; i<arExtraPaidDuringProcessing.length; i++)
		{
			lDetails.get(i).setExtraPaidDuringProcessing(new MoneyInteger(arExtraPaidDuringProcessing[i]));
		}
		
		idx = 0;
		for (int iCurrent : arValues)
		{
			lDetails.get(idx++).setAmount(iCurrent);
		}
	}
	
	private static int[] adjustWeightedSumToTarget(int iTarget, int[] arValues, int[] arExtraPaidBeforeProcessing)
	{
		if ((null == arValues) || (0 == arValues.length))
		{
			throw new InvalidParameterException("Percentage values array cannot be null or empty");
		}
		if ((null == arExtraPaidBeforeProcessing) || (0 == arExtraPaidBeforeProcessing.length))
		{
			throw new InvalidParameterException("Extra paid before processing array cannot be null or empty");
		}
		if (arValues.length != arExtraPaidBeforeProcessing.length)
		{
			throw new InvalidParameterException("Percentage values array (" + arValues.length + ") and extra paid before processing array (" + arExtraPaidBeforeProcessing.length + ") cannot be of different lengths");
		}
		
		int[] arExtraPaidDuringProcessing = new int[arExtraPaidBeforeProcessing.length];
		
		// If target is zero, set all to zero and return
		if (0 == iTarget)
		{
			for (int i=0; i<arValues.length; i++)
			{
				arValues[i] =0;
			}
			return arExtraPaidDuringProcessing;
		}
		
		// If all percentage values are zeros, set to all 1's
		boolean bNonZeroFound = false;
		// Ensure all numbers are zero or positive
		for (int i=0; i<arValues.length; i++)
		{
			if (0 > arValues[i])
			{
				throw new InvalidParameterException("Negative percantage value not allowed: " + arValues[i]);
			}
			else if (0 != arValues[i])
			{
				bNonZeroFound = true;
			}
		}
		
		if (!bNonZeroFound)
		{
			for (int i=0; i<arValues.length; i++)
			{
				arValues[i] = 1;
			}
		}
			
		double dCurrentSum = 0.0;
		double[] arWorkingValues = new double[arValues.length];
		for (int i=0; i<arValues.length; i++)
		{
			arWorkingValues[i] = (double)arValues[i];
			dCurrentSum += arWorkingValues[i];
		}
		
		for (int i=0; i<arValues.length; i++)
		{
			arWorkingValues[i] = (arWorkingValues[i] / dCurrentSum) * (double)iTarget;
		}
		
		// Get integer sum
		int iWorkingSum = 0;
		for (int i=0; i<arValues.length; i++)
		{
			iWorkingSum += new Double(arWorkingValues[i]).intValue();
		}
			
		while (iWorkingSum < iTarget)
		{
			int iDiff = iTarget - iWorkingSum;
			double[] arWorkingHundredths = new double[arValues.length];
			for (int i=0; i<arWorkingValues.length; i++)
			{
				if (arWorkingValues[i] != 0.0)
				{
					arWorkingHundredths[i] = (arWorkingValues[i] - new Double(arWorkingValues[i]).intValue());
				}
			}

			for (double d = 0.75; (d >= 0.0) && (iDiff > 0); d -= 0.25)
			{	// Attempt to randomize which of multiple entries gets the additional value
				List<Integer> lInRangeIndexes = new ArrayList<Integer>();
				for (int i=0; i<arWorkingValues.length; i++)
				{
					if (arWorkingHundredths[i] >= d)
					{
						lInRangeIndexes.add(new Integer(i));
					}
				}
				if (0 != lInRangeIndexes.size())
				{
					if (iDiff >= lInRangeIndexes.size())
					{	// All in range entries will get incremented
						for (int i=0; i<arWorkingValues.length && (iDiff > 0); i++)
						{
							if (arWorkingHundredths[i] > d)
							{
								arWorkingHundredths[i] = 0;
								arWorkingValues[i] = new Double(arWorkingValues[i]).intValue() + 1;
								iDiff--;
							}
						}
					}
					else
					{	// Only some of the in range entries will get incremented,
						// determine which ones.
						int[] sortedByWeight = sortByWeight(arExtraPaidBeforeProcessing, lInRangeIndexes, false);
						for (int r=0; r<sortedByWeight.length  && (iDiff > 0); r++)
						{
							int iSortedIdx = sortedByWeight[r];
							arWorkingHundredths[iSortedIdx] = 0;
							arWorkingValues[iSortedIdx] = new Double(arWorkingValues[iSortedIdx]).intValue() + 1;
							arExtraPaidDuringProcessing[iSortedIdx]++;
							iDiff--;
						}				
					}
				}
			}
			
			iWorkingSum = 0;
			for (int i=0; i<arWorkingValues.length; i++)
			{
				iWorkingSum += new Double(arWorkingValues[i]).intValue();
			}
		}
			
		// Now truncate all working double values because their integer values
		// are what is required to return.
		for (int i=0; i<arValues.length; i++)
		{
			arValues[i] = new Double(arWorkingValues[i]).intValue();
		}
		
		return arExtraPaidDuringProcessing;
	}
	
	private static int[] sortByWeight(int[] arWeights, List<Integer> lIndexesIntoWeights, boolean bHighWeightSortsFirst)
	{
		if (null == lIndexesIntoWeights)
		{
			throw new InvalidParameterException("Cannot have a null array of indexes!");
		}
		int[] arIndexesIntoWeights = new int[lIndexesIntoWeights.size()];
		for (int i=0; i<lIndexesIntoWeights.size(); i++)
		{
			arIndexesIntoWeights[i] = lIndexesIntoWeights.get(i).intValue();
		}
		return sortByWeight(arWeights, arIndexesIntoWeights, bHighWeightSortsFirst);
	}
	
	/**
	 * 
	 * @param arWeights The weights assigned to each index. The size of this
	 * array MUST always be equal to or greater than the size of the {@code 
	 * arIndexesIntoWeights} parameter.
	 * @param arIndexesIntoWeights The indexes into {@code arWeights} that 
	 * will be sorted by weight order into a new resultant array.
	 * @param bHighWeightSortsFirst If {@code true} then the indexes will be
	 * sorted assuming that the higher the weight, the sooner the index will
	 * appear in the result. If {@code false}, then lower weights will cause
	 * indexes to sort sooner in the result.
	 * @return A newly allocated List containing the sorted indexes.
	 */
	private static int[] sortByWeight(int[] arWeights, int[] arIndexesIntoWeights, boolean bHighWeightSortsFirst)
	{
		if ((null == arWeights) || (null == arIndexesIntoWeights))
		{
			throw new InvalidParameterException("Cannot have a null array of weights or indexes!");
		}
		if (0 == arIndexesIntoWeights.length)
		{
			throw new InvalidParameterException("Indexes array cannot be empty!");
		}
		if (0 == arWeights.length)
		{
			throw new InvalidParameterException("Weights array cannot be empty!");
		}
		if (arIndexesIntoWeights.length > arWeights.length)
		{
			throw new InvalidParameterException("Indexes array cannot be larger than the weights array!");
		}
		int[] arWeightForIndex = new int[arIndexesIntoWeights.length];
		// Populate the weight per index array
		int iMaxWeight = -1;
		for (int i=0; i<arIndexesIntoWeights.length; i++)
		{
			arWeightForIndex[i] = arWeights[arIndexesIntoWeights[i]];
			if (iMaxWeight < arWeightForIndex[i])
			{
				iMaxWeight = arWeightForIndex[i];
			}
		}
		// Now, starting with the maximum found weight, scan the "weight for index"
		// array arranging the associated indexes in sorted order, from high weight
		// to low weight
		int[] arSorted = new int[arWeightForIndex.length];
		int insertIdx = 0;
		while (0 <= iMaxWeight)
		{
			for (int i=0; i<arWeightForIndex.length; i++)
			{
				if (arWeightForIndex[i] == iMaxWeight)
				{
					arSorted[insertIdx++] =  arIndexesIntoWeights[i];
				}
			}
			iMaxWeight--;
		}
		if (!bHighWeightSortsFirst)
		{	// Reverse the results to sort low weight to high weight
			int[] aReverseSorted = new int[arSorted.length];
			int idxInsertReverse = 0;
			for (int i=(arSorted.length - 1); i>=0; i--)
			{
				aReverseSorted[idxInsertReverse++] = arSorted[i];
			}
			arSorted = aReverseSorted;
		}
		return arSorted;
	}
	
	/*
	public static void main(String[] args)
	{
		try
		{
			int[] arWeights = new int[]{5,2};
			int[] arIndexesIntoWeights = new int[]{0, 1};
			int[] arResult = sortByWeight(arWeights, arIndexesIntoWeights, true);
			for (int i : arResult)
			{
				System.out.print(i + ", ");
			}
			System.out.println("");
			
			int[] arChargedExtra = new int[]{3, 2, 3};
			adjustWeightedSumToTarget(100, new int[]{100, 100, 100}, arChargedExtra);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		int[] t = new int[]{0,0,0,0};
		adjustWeightedSumToTarget(1, t);
		
		for (int iTarget=0; iTarget<1001; iTarget++)
		{
			System.out.println("Target: " + iTarget);
			for (int iSlot1=0; iSlot1<1001; iSlot1++)
			{
				for (int iSlot2=0; iSlot2<1001; iSlot2++)
				{
					for (int iSlot3=0; iSlot3<1001; iSlot3++)
					{
						for (int iSlot4=0; iSlot4<1001; iSlot4++)
						{
							int[] v1 = new int[]{iSlot1, iSlot2, iSlot3, iSlot4};
							adjustWeightedSumToTarget(iTarget, v1);
							if ((v1[0] + v1[1] + v1[2] + v1[3]) != iTarget)
							{
								System.out.println("Fail: Target: " + iTarget + ", [" + iSlot1 + ", " + iSlot2 + ", " + iSlot3 + ", " + iSlot4 + "]");
								System.out.println("... Returned: [1: " + v1[0] + ", 2: " + v1[1] + ", 3: " + v1[2] + ", 4: " + v1[3] + "]");
							}
						}
					}
				}
			}
		}
		System.out.println("Done");
	}
	*/		
		
	
}
