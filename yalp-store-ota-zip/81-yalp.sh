#!/sbin/sh

. /tmp/backuptool.functions

PACKAGE_NAME=YalpStore

list_files() {
cat <<EOF
app/${PACKAGE_NAME}.apk
app/${PACKAGE_NAME}/${PACKAGE_NAME}.apk
priv-app/${PACKAGE_NAME}
priv-app/${PACKAGE_NAME}/${PACKAGE_NAME}.apk
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
