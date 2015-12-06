package eventManagement;

import java.util.Comparator;

public class EventComparator implements Comparator<Event>
{
    @Override
    public int compare( Event a, Event b )
    {
	if ( compareTime( a, b ) != 0 )
	{
	    return compareTime( a, b );
	}
	else
	{
	    return comparePriority( a, b );
	}
    }

    private int compareTime( Event a, Event b )
    {
	if ( a.timeStamp < b.timeStamp )
	{
	    return -1;
	}
	else if ( a.timeStamp > b.timeStamp )
	{
	    return 1;
	}
	else
	{
	    return 0;
	}
    }

    private int comparePriority( Event a, Event b )
    {
	if ( a.eventPriority.ordinal() < b.eventPriority.ordinal() )
	{
	    return -1;
	}
	else if ( a.eventPriority.ordinal() > b.eventPriority.ordinal() )
	{
	    return 1;
	}
	else
	{
	    return 0;
	}
    }
}