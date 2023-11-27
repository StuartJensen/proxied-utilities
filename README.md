# proxied-utilities
For landlords. A utilities account ledger assigning shared costs to multiple responsible tenants.
# The Problem
In an apartment where multiple singles rent rooms, and where the turnover is high, keeping the utilities account in the name of one of the tenants causes issues:
When the tenant owning the utilities account moves out, the account(s) must be transferred to another tenant.
A transfer fee is often charged to move the account into a different tenant's name.
# The Solution
Keep the utilities accounts in the landlord's name. Include in the apartment lease that the tenants are responsible to pay the utilities. This way, tenants leaving does not require changes to any utility accounts.
# The New Problem
How does the landlord keep track of how much each tenant owes for each utility bill? Utility service periods rarely fall on month boundaries and many times tenants move out in the middle of a month.
# The New Solution
Proxied Utilities keeps track of how much each tenant owes to each utility bill.  Reports may be run to show a given tenant's ledger with their cuirrent balance.
# The Overhead
A proxied utilities ledger file must be created/maintained by the landlord and the proxied utilities executable must be run to create reports for the tenants.<br>
<br>
<b>A sample proxied utilities ledger file:</b>
<pre>
08/01/2016, PAYEE, Midland Gas
08/01/2016, TENANT, Mickey Mouse
08/01/2016, DEPOSIT, Mickey Mouse, 35.00
08/01/2016, TENANT, Donald Duck
08/01/2016, DEPOSIT, Donald Duck, 35.00
08/21/2016, PAYMENT, Midland Gas, 32.09, 08/01/2016, 09/03/2016
09/01/2016, DEPOSIT, Mickey Mouse, 35.00
09/01/2016, DEPOSIT, Donald Duck, 35.00
09/11/2016, PAYMENT, Midland Gas, 14.93, 09/04/2016, 10/04/2016
10/01/2016, DEPOSIT, Mickey Mouse, 35.00
10/01/2016, DEPOSIT, Donald Duck, 35.00
10/13/2016, PAYMENT, Midland Gas, 23.77, 10/05/2016, 11/02/2016
11/01/2016, MOVEOUT, Donald Duck
11/04/2016, BALANCE, Donald Duck, 0.00
11/05/2016, REMOVE, Donald Duck
</pre>
<b>A sample executable command line to get a ledger report for Domald Duck:</b>
<pre>
java -jar ProxiedUtilities.jar -inFile UtilityLedger.txt -outFile ReportTenantDonaldDuck.txt -report ledger -target "Donald Duck"

<b>Output:</b>

LEDGER Report Generated on 11/26/2023 at 19:32:32
On behalf of tenant Donald Duck
Date: 08/01/2016
    Register: TenantEntry, Donald Duck
    Paid: Midland Gas: $0.48
    Register: DepositEntry, Tenant: Donald Duck, Amount: 35.00
    Balance: 34.52
Date: 08/02/2016
    Paid: Midland Gas: $0.47
    Balance: 34.05
Date: 08/03/2016
    Paid: Midland Gas: $0.48
    Balance: 33.57
Date: 08/04/2016
    Paid: Midland Gas: $0.47
    Balance: 33.10
Date: 08/05/2016
    Paid: Midland Gas: $0.48
    Balance: 32.62
Date: 08/06/2016
    Paid: Midland Gas: $0.47
    Balance: 32.15
Date: 08/07/2016
    Paid: Midland Gas: $0.48
    Balance: 31.67
Date: 08/08/2016
    Paid: Midland Gas: $0.47
    Balance: 31.20
Date: 08/09/2016
    Paid: Midland Gas: $0.48
    Balance: 30.72
Date: 08/10/2016
    Paid: Midland Gas: $0.47
    Balance: 30.25
Date: 08/11/2016
    Paid: Midland Gas: $0.48
    Balance: 29.77
Date: 08/12/2016
    Paid: Midland Gas: $0.47
    Balance: 29.30
Date: 08/13/2016
    Paid: Midland Gas: $0.48
    Balance: 28.82
Date: 08/14/2016
    Paid: Midland Gas: $0.47
    Balance: 28.35
Date: 08/15/2016
    Paid: Midland Gas: $0.47
    Balance: 27.88
Date: 08/16/2016
    Paid: Midland Gas: $0.47
    Balance: 27.41
Date: 08/17/2016
    Paid: Midland Gas: $0.47
    Balance: 26.94
Date: 08/18/2016
    Paid: Midland Gas: $0.47
    Balance: 26.47
Date: 08/19/2016
    Paid: Midland Gas: $0.47
    Balance: 26.00
Date: 08/20/2016
    Paid: Midland Gas: $0.47
    Balance: 25.53
Date: 08/21/2016
    Paid: Midland Gas: $0.47
    Balance: 25.06
Date: 08/22/2016
    Paid: Midland Gas: $0.47
    Balance: 24.59
Date: 08/23/2016
    Paid: Midland Gas: $0.47
    Balance: 24.12
Date: 08/24/2016
    Paid: Midland Gas: $0.47
    Balance: 23.65
Date: 08/25/2016
    Paid: Midland Gas: $0.47
    Balance: 23.18
Date: 08/26/2016
    Paid: Midland Gas: $0.47
    Balance: 22.71
Date: 08/27/2016
    Paid: Midland Gas: $0.47
    Balance: 22.24
Date: 08/28/2016
    Paid: Midland Gas: $0.47
    Balance: 21.77
Date: 08/29/2016
    Paid: Midland Gas: $0.47
    Balance: 21.30
Date: 08/30/2016
    Paid: Midland Gas: $0.47
    Balance: 20.83
Date: 08/31/2016
    Paid: Midland Gas: $0.47
    Balance: 20.36
Date: 09/01/2016
    Paid: Midland Gas: $0.47
    Register: DepositEntry, Tenant: Donald Duck, Amount: 35.00
    Balance: 54.89
Date: 09/02/2016
    Paid: Midland Gas: $0.47
    Balance: 54.42
Date: 09/03/2016
    Paid: Midland Gas: $0.47
    Balance: 53.95
Date: 09/04/2016
    Paid: Midland Gas: $0.24
    Balance: 53.71
Date: 09/05/2016
    Paid: Midland Gas: $0.25
    Balance: 53.46
Date: 09/06/2016
    Paid: Midland Gas: $0.24
    Balance: 53.22
Date: 09/07/2016
    Paid: Midland Gas: $0.25
    Balance: 52.97
Date: 09/08/2016
    Paid: Midland Gas: $0.24
    Balance: 52.73
Date: 09/09/2016
    Paid: Midland Gas: $0.24
    Balance: 52.49
Date: 09/10/2016
    Paid: Midland Gas: $0.24
    Balance: 52.25
Date: 09/11/2016
    Paid: Midland Gas: $0.24
    Balance: 52.01
Date: 09/12/2016
    Paid: Midland Gas: $0.24
    Balance: 51.77
Date: 09/13/2016
    Paid: Midland Gas: $0.24
    Balance: 51.53
Date: 09/14/2016
    Paid: Midland Gas: $0.24
    Balance: 51.29
Date: 09/15/2016
    Paid: Midland Gas: $0.24
    Balance: 51.05
Date: 09/16/2016
    Paid: Midland Gas: $0.24
    Balance: 50.81
Date: 09/17/2016
    Paid: Midland Gas: $0.24
    Balance: 50.57
Date: 09/18/2016
    Paid: Midland Gas: $0.24
    Balance: 50.33
Date: 09/19/2016
    Paid: Midland Gas: $0.24
    Balance: 50.09
Date: 09/20/2016
    Paid: Midland Gas: $0.24
    Balance: 49.85
Date: 09/21/2016
    Paid: Midland Gas: $0.24
    Balance: 49.61
Date: 09/22/2016
    Paid: Midland Gas: $0.24
    Balance: 49.37
Date: 09/23/2016
    Paid: Midland Gas: $0.24
    Balance: 49.13
Date: 09/24/2016
    Paid: Midland Gas: $0.24
    Balance: 48.89
Date: 09/25/2016
    Paid: Midland Gas: $0.24
    Balance: 48.65
Date: 09/26/2016
    Paid: Midland Gas: $0.24
    Balance: 48.41
Date: 09/27/2016
    Paid: Midland Gas: $0.24
    Balance: 48.17
Date: 09/28/2016
    Paid: Midland Gas: $0.24
    Balance: 47.93
Date: 09/29/2016
    Paid: Midland Gas: $0.24
    Balance: 47.69
Date: 09/30/2016
    Paid: Midland Gas: $0.24
    Balance: 47.45
Date: 10/01/2016
    Paid: Midland Gas: $0.24
    Register: DepositEntry, Tenant: Donald Duck, Amount: 35.00
    Balance: 82.21
Date: 10/02/2016
    Paid: Midland Gas: $0.24
    Balance: 81.97
Date: 10/03/2016
    Paid: Midland Gas: $0.24
    Balance: 81.73
Date: 10/04/2016
    Paid: Midland Gas: $0.24
    Balance: 81.49
Date: 10/05/2016
    Paid: Midland Gas: $0.41
    Balance: 81.08
Date: 10/06/2016
    Paid: Midland Gas: $0.41
    Balance: 80.67
Date: 10/07/2016
    Paid: Midland Gas: $0.41
    Balance: 80.26
Date: 10/08/2016
    Paid: Midland Gas: $0.41
    Balance: 79.85
Date: 10/09/2016
    Paid: Midland Gas: $0.41
    Balance: 79.44
Date: 10/10/2016
    Paid: Midland Gas: $0.41
    Balance: 79.03
Date: 10/11/2016
    Paid: Midland Gas: $0.41
    Balance: 78.62
Date: 10/12/2016
    Paid: Midland Gas: $0.41
    Balance: 78.21
Date: 10/13/2016
    Paid: Midland Gas: $0.41
    Balance: 77.80
Date: 10/14/2016
    Paid: Midland Gas: $0.41
    Balance: 77.39
Date: 10/15/2016
    Paid: Midland Gas: $0.41
    Balance: 76.98
Date: 10/16/2016
    Paid: Midland Gas: $0.41
    Balance: 76.57
Date: 10/17/2016
    Paid: Midland Gas: $0.41
    Balance: 76.16
Date: 10/18/2016
    Paid: Midland Gas: $0.41
    Balance: 75.75
Date: 10/19/2016
    Paid: Midland Gas: $0.41
    Balance: 75.34
Date: 10/20/2016
    Paid: Midland Gas: $0.41
    Balance: 74.93
Date: 10/21/2016
    Paid: Midland Gas: $0.41
    Balance: 74.52
Date: 10/22/2016
    Paid: Midland Gas: $0.41
    Balance: 74.11
Date: 10/23/2016
    Paid: Midland Gas: $0.41
    Balance: 73.70
Date: 10/24/2016
    Paid: Midland Gas: $0.41
    Balance: 73.29
Date: 10/25/2016
    Paid: Midland Gas: $0.41
    Balance: 72.88
Date: 10/26/2016
    Paid: Midland Gas: $0.41
    Balance: 72.47
Date: 10/27/2016
    Paid: Midland Gas: $0.41
    Balance: 72.06
Date: 10/28/2016
    Paid: Midland Gas: $0.41
    Balance: 71.65
Date: 10/29/2016
    Paid: Midland Gas: $0.41
    Balance: 71.24
Date: 10/30/2016
    Paid: Midland Gas: $0.41
    Balance: 70.83
Date: 10/31/2016
    Paid: Midland Gas: $0.41
    Balance: 70.42
Date: 11/01/2016
    Register: MoveOutEntry, Tenant: Donald Duck
    Paid: Midland Gas: $0.41
    Balance: 70.01
Date: 11/02/2016
    Balance: 70.01
Date: 11/03/2016
    Balance: 70.01
Date: 11/04/2016
    Register: BalanceEntry, Tenant: Donald Duck, Amount: 0.00
    Balance: 0.00
Date: 11/05/2016
    Register: RemoveEntry, Target: Donald Duck
</pre>
