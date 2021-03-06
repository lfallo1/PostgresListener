-- create a cron job that runs once a day, and executes the uw.checkinactive() function
-- this function loops through each account. if an account is exactly 173 days old (1 week shy of 6 months), 
-- it emits a 'mygeneinactive' notification with one parameter (the account username).
-- a java app on the server that is always executing and listening for this event, handles it by sending an email to that user.

CREATE OR REPLACE FUNCTION uw.notifyaccountinactive()
  RETURNS integer AS
$BODY$
DECLARE
    a uw.account%rowtype;
BEGIN
    FOR a IN SELECT * FROM uw.account
    WHERE full_access = false and ((now()::date - datecreated::date) = 173)
    LOOP
        execute 'NOTIFY accountinactive, ''' || a.username || '''';
    END LOOP;
    RETURN 1;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION uw.notifyaccountinactive()
  OWNER TO ${owner};


-- similar to above, but notifiers of account deleted
CREATE OR REPLACE FUNCTION uw.notifyaccountdeleted()
  RETURNS integer AS
$BODY$
DECLARE
    a uw.account%rowtype;
BEGIN
    FOR a IN SELECT * FROM uw.account
    WHERE full_access = false and ((now()::date - datecreated::date) > 180)
    LOOP
        execute 'NOTIFY accountdeleted, ''' || a.username || '''';
    END LOOP;
    RETURN 1;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION uw.notifyaccountdeleted()
  OWNER TO ${owner};
  
DROP FUNCTION uw.deleteinactiveclinician();
DROP FUNCTION uw.deleteinactivefamily();