#!/sbin/sh
# 
# /system/addon.d/81-yalp.sh
# During a system upgrade, this script backs up Yalp Store apk,
# /system is formatted and reinstalled, then the file is restored.
#

. /tmp/backuptool.functions
list_files() {
cat <<EOF
app/YalpStore.apk
app/YalpStore/YalpStore.apk
priv-app/YalpStore
priv-app/YalpStore/YalpStore.apk
EOF
}
case "$1" in
  backup)
    list_files | while read FILE DUMMY; do
      backup_file $S/"$FILE"
    done
  ;;
  restore)
    list_files | while read FILE REPLACEMENT; do
      R=""
      [ -n "$REPLACEMENT" ] && R="$S/$REPLACEMENT"
      [ -f "$C/$S/$FILE" ] && restore_file $S/"$FILE" "$R"
    done
  ;;
  pre-backup)
    # Stub
  ;;
  post-backup)
    # Stub
  ;;
  pre-restore)
    # Stub
  ;;
  post-restore)
    # Stub
  ;;
esac