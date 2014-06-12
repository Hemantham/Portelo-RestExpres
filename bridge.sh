echo " "
#read -p "Bridge Acct UserName: " user
#echo " "
ssh -L   1995:dbs-b2-0101:1521 -L  1994:dbs-b2-0105:1521 -v -l UMUDAHE -N bridge.pearsoncmg.com
