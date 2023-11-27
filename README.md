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
Proxied Utilities keeps track of how much each tenant owes to each utility bill.  Reports may be run to show a given tenant's ledger with their current balance.
# The Overhead
A proxied utilities ledger file must be created/maintained by the landlord and the proxied utilities executable must be run to create reports for the tenants.<br>
<br>
<b>A sample proxied utilities ledger file:</b>
<pre>
08/01/2016, PAYEE, Midland Gas, MG
08/01/2016, PAYEE, Outland Electric, OE
08/01/2016, TENANT, Mickey Mouse
08/01/2016, DEPOSIT, Mickey Mouse, 35.00
08/01/2016, TENANT, Donald Duck
08/01/2016, DEPOSIT, Donald Duck, 35.00
08/21/2016, PAYMENT, Midland Gas, 32.09, 08/01/2016, 09/03/2016
08/26/2016, PAYMENT, Outland Electric, 7.88, 08/01/2016, 08/13/2016
09/01/2016, DEPOSIT, Mickey Mouse, 35.00
09/01/2016, DEPOSIT, Donald Duck, 35.00
09/11/2016, PAYMENT, Midland Gas, 14.93, 09/04/2016, 10/04/2016
09/21/2016, PAYMENT, Outland Electric, 51.11, 08/14/2016, 09/13/2016
10/01/2016, DEPOSIT, Mickey Mouse, 35.00
10/01/2016, DEPOSIT, Donald Duck, 35.00
10/13/2016, PAYMENT, Midland Gas, 23.77, 10/05/2016, 11/02/2016
10/22/2016, PAYMENT, Outland Electric, 48.77, 09/14/2016, 10/12/2016
11/01/2016, MOVEOUT, Donald Duck
11/03/2016, DEPOSIT, Donald Duck, 10.06
11/05/2016, REMOVE, Donald Duck
11/18/2016, PAYMENT, Outland Electric, 66.23, 10/13/2016, 11/09/2016
</pre>
<b>A sample executable command line to get a ledger report for Domald Duck:</b>
<pre>
java -jar ProxiedUtilities.jar -inFile UtilityLedger.txt -outFile ReportTenantDonaldDuck.txt -report ledger -target "Donald Duck"

<b>Output:</b>

LEDGER Report Generated on 11/26/2023 at 19:42:17
On behalf of tenant Donald Duck
Date: 08/01/2016
    Register: TenantEntry, Donald Duck
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Register: DepositEntry, Tenant: Donald Duck, Amount: 35.00
    Balance: 34.22
Date: 08/02/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 33.44
Date: 08/03/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 32.66
Date: 08/04/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 31.88
Date: 08/05/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 31.10
Date: 08/06/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 30.32
Date: 08/07/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 29.54
Date: 08/08/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 28.76
Date: 08/09/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 27.98
Date: 08/10/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.30
    Balance: 27.21
Date: 08/11/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 26.43
Date: 08/12/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.30
    Balance: 25.66
Date: 08/13/2016
    Paid: Midland Gas: $0.48
    Paid: Outland Electric: $0.30
    Balance: 24.88
Date: 08/14/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 23.59
Date: 08/15/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 22.29
Date: 08/16/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 21.00
Date: 08/17/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 19.70
Date: 08/18/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 18.41
Date: 08/19/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 17.11
Date: 08/20/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 15.82
Date: 08/21/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 14.52
Date: 08/22/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 13.23
Date: 08/23/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 11.93
Date: 08/24/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 10.64
Date: 08/25/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 9.34
Date: 08/26/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 8.05
Date: 08/27/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 6.75
Date: 08/28/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 5.46
Date: 08/29/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 4.16
Date: 08/30/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 2.87
Date: 08/31/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 1.57
Date: 09/01/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Register: DepositEntry, Tenant: Donald Duck, Amount: 35.00
    Balance: 35.28
Date: 09/02/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.83
    Balance: 33.98
Date: 09/03/2016
    Paid: Midland Gas: $0.47
    Paid: Outland Electric: $0.82
    Balance: 32.69
Date: 09/04/2016
    Paid: Midland Gas: $0.25
    Paid: Outland Electric: $0.82
    Balance: 31.62
Date: 09/05/2016
    Paid: Midland Gas: $0.25
    Paid: Outland Electric: $0.82
    Balance: 30.55
Date: 09/06/2016
    Paid: Midland Gas: $0.25
    Paid: Outland Electric: $0.82
    Balance: 29.48
Date: 09/07/2016
    Paid: Midland Gas: $0.25
    Paid: Outland Electric: $0.82
    Balance: 28.41
Date: 09/08/2016
    Paid: Midland Gas: $0.25
    Paid: Outland Electric: $0.82
    Balance: 27.34
Date: 09/09/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.83
    Balance: 26.27
Date: 09/10/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.82
    Balance: 25.21
Date: 09/11/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.82
    Balance: 24.15
Date: 09/12/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.82
    Balance: 23.09
Date: 09/13/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.82
    Balance: 22.03
Date: 09/14/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 20.95
Date: 09/15/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.85
    Balance: 19.86
Date: 09/16/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 18.78
Date: 09/17/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.85
    Balance: 17.69
Date: 09/18/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 16.61
Date: 09/19/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 15.53
Date: 09/20/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 14.45
Date: 09/21/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 13.37
Date: 09/22/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 12.29
Date: 09/23/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 11.21
Date: 09/24/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 10.13
Date: 09/25/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 9.05
Date: 09/26/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 7.97
Date: 09/27/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 6.89
Date: 09/28/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 5.81
Date: 09/29/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 4.73
Date: 09/30/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 3.65
Date: 10/01/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Register: DepositEntry, Tenant: Donald Duck, Amount: 35.00
    Balance: 37.57
Date: 10/02/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 36.49
Date: 10/03/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 35.41
Date: 10/04/2016
    Paid: Midland Gas: $0.24
    Paid: Outland Electric: $0.84
    Balance: 34.33
Date: 10/05/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 33.08
Date: 10/06/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 31.83
Date: 10/07/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 30.58
Date: 10/08/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 29.33
Date: 10/09/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 28.08
Date: 10/10/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 26.83
Date: 10/11/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 25.58
Date: 10/12/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $0.84
    Balance: 24.33
Date: 10/13/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 22.74
Date: 10/14/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 21.14
Date: 10/15/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 19.55
Date: 10/16/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 17.95
Date: 10/17/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 16.36
Date: 10/18/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 14.76
Date: 10/19/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 13.17
Date: 10/20/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 11.57
Date: 10/21/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 9.98
Date: 10/22/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 8.38
Date: 10/23/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 6.79
Date: 10/24/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 5.19
Date: 10/25/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 3.60
Date: 10/26/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.19
    Balance: 2.00
Date: 10/27/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: 0.41
Date: 10/28/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: -1.18
Date: 10/29/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: -2.77
Date: 10/30/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: -4.36
Date: 10/31/2016
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: -5.95
Date: 11/01/2016
    Register: MoveOutEntry, Tenant: Donald Duck
    Paid: Midland Gas: $0.41
    Paid: Outland Electric: $1.18
    Balance: -7.54
Date: 11/02/2016
    Balance: -7.54
Date: 11/03/2016
    Register: DepositEntry, Tenant: Donald Duck, Amount: 10.06
    Balance: 2.52
Date: 11/04/2016
    Balance: 2.52
Date: 11/05/2016
    Register: RemoveEntry, Target: Donald Duck
</pre>
