#!/bin/bash -e

#
# <script> num200 num300 intervalLength
#
# Builds 300 records from 2025-01-x for each day of that January.
# Limited to that January; 31 days of data per nmi.

num200=$1 # ... AKA each customer meter
num300=$2 # ... AKA the day of month
intervalLength=$3 # 5|15|30
numConsumptions=$((1440 / $3))


echo "num200=$num200" >&2
echo "num300=$num300" >&2
echo "intervalLength=$3" >&2
echo "numConsumptions=$numConsumptions" >&2

echo '100,NEM12,200301011534,MDP1,Retailer1'

for ((i = 1 ; i <= $num200 ; i++)); do

  nmi="NMI_$i"

  # RecordIndicator,NMI,NMIConfiguration,RegisterID,NMISuffix,MDMDataStreamIdentifier,MeterSerialNumber,UOM,IntervalLength,NextScheduledReadDate
	echo "200,$nmi,E1Q1,Register_$i,suffix,id,meterSn_$i,kWh,$intervalLength,20040120"

	for ((j = 1 ; j <= $num300 ; j++)); do

	  intervalDate=$(printf '%02d' $j)
	  consumptionString=''

	  for ((k = 1 ; k <= $numConsumptions ; k++)); do
	    consumptionString="$consumptionString,$k.3"
	  done
	  consumptionStringLen=${#consumptionString}
	  consumptionStringLenTrimmed=$(($consumptionStringLen - 1))
	  consumptionString=${consumptionString:1:$consumptionStringLenTrimmed}
	  #echo $consumptionString

    # RecordIndicator,IntervalDate,IntervalValue1...IntervalValueN,QualityMethod,ReasonCode,ReasonDescription,UpdateDateTime,MSATSLoadDateTime
    # 300,20030501,50.1, . . . ,21.5,V,,,20030101153445,20030102023012
    echo "300,2501$intervalDate,$consumptionString,V,,,20030101153445,20030102023012"
	done

done

echo "900"
