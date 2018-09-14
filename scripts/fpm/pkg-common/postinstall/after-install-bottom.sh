INIT=`readlink -fn /sbin/init`
if [[ $INIT == */systemd ]]; then
  rm -f /etc/init.d/spm-monitor
  if [ -e /lib/systemd/system/spm-monitor.service ]; then
    systemctl enable spm-monitor
    systemctl restart spm-monitor
  fi
else
  if [ -e /usr/sbin/update-rc.d ]; then
    update-rc.d spm-monitor start 20 2 3 4 5 . stop 80 0 1 6 .
  elif [ -e /sbin/chkconfig ]; then
    chkconfig --add spm-monitor
    chkconfig spm-monitor on
  fi
  /etc/init.d/spm-monitor restart
fi

echo
echo -------------------------------------------------------------------------------
echo
echo SPM client installer done
echo
tput setab 2
tput setaf 7
echo "                                                                                                                   "
echo "IMPORTANT: If doing a first installation on this machine, remember to complete the remaining installation          "
echo "           steps (from step 2) listed at: https://apps.sematext.com/ui/monitoring (click Actions->Install Monitor) "
echo "                                                                                                                   "
tput op
tput setab 3
tput setaf 0
echo
echo "WARN: If your server accesses Internet via proxy, set proxy host, port, username by running:"
echo "  sudo bash /opt/spm/bin/setup-env --proxy-host \"www.myproxyhost.com\" --proxy-port \"1234\" --proxy-user \"myuser\" --proxy-password \"mypassword123\""
tput op
echo
echo When done with all steps, go to https://apps.sematext.com/ui/monitoring to see your performance charts
echo If you do not see your graphs in a few minutes, run: sudo bash /opt/spm/bin/spm-client-diagnostics.sh
echo
echo -------------------------------------------------------------------------------
echo

exit 0
