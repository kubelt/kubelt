import React, { useEffect, useState } from "react";
import Layout from "../AppLayout";

import useProfile from "../../hooks/profile";
import { View, Text, StyleSheet } from "react-native";

import { Entypo } from "@expo/vector-icons";
import useAccount from "../../hooks/account";

export default function Details({
  navigation,
}: {
  children: any;
  navigation: any;
}) {
  const account = useAccount();

  const persistedProfile = useProfile();
  const [profile, setProfile] = useState(persistedProfile);

  useEffect(() => {
    setProfile(persistedProfile);
  }, [persistedProfile]);

  return (
    <Layout navigation={navigation}>
      <View
        style={{
          position: "absolute",
          flex: 1,
          height: "33%",
          backgroundColor: "#192030",
          left: 0,
          right: 0,
          zIndex: -1,
        }}
      ></View>

      <View
        style={{
          flex: 1,
          marginHorizontal: "5em",
        }}
      >
        <View
          style={{
            flexDirection: "row",
          }}
        >
          <View
            style={{
              width: 286,
              height: 404,
              backgroundColor: "white",
              shadowRadius: 5,
              shadowColor: "rgb(0, 0, 0)",
              shadowOpacity: 0.2,
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            {profile.nickname && (
              <Text style={styles.card.nickname}>{profile.nickname}</Text>
            )}
            {profile.nickname && account && (
              <Text>{`${account.substring(0, 4)}...${account.substring(
                account.length - 6
              )}`}</Text>
            )}
          </View>

          <View
            style={{
              flex: 1,
            }}
          >
            <View
              style={{
                flex: 1,
              }}
            ></View>
            <View
              style={{
                flex: 1,
                padding: "1em",
              }}
            >
              <View
                style={{
                  flex: 1,
                }}
              >
                <Text>{profile.bio}</Text>
              </View>

              <hr />

              <View
                style={{
                  display: "flex",
                  flexDirection: "row",
                  justifyContent: "space-between",
                }}
              >
                <View
                  style={{
                    display: "flex",
                    flexDirection: "row",
                    flex: 1,
                  }}
                >
                  {profile.location && (
                    <View style={styles.field.wrapper}>
                      <Entypo
                        style={styles.field.icon}
                        name="location-pin"
                        size={16}
                        color="#9CA3AF"
                      />

                      <Text style={styles.field.text}>{profile.location}</Text>
                    </View>
                  )}

                  {profile.job && (
                    <View style={styles.field.wrapper}>
                      <Entypo
                        style={styles.field.icon}
                        name="suitcase"
                        size={16}
                        color="#9CA3AF"
                      />

                      <Text style={styles.field.text}>{profile.job}</Text>
                    </View>
                  )}
                </View>

                <View>
                  <Text>Edit profile</Text>
                </View>
              </View>
            </View>
          </View>
        </View>

        <Text>
          <pre>{JSON.stringify(profile, null, 2)}</pre>
        </Text>
      </View>
    </Layout>
  );
}

const styles = {
  card: {
    nickname: {
      fontFamily: "Manrope_700Bold",
      fontSize: 24,
      lineHeight: 32.78,
    },
  },
  field: StyleSheet.create({
    wrapper: {
      flexDirection: "row",
      alignItems: "center",
      paddingRight: "2em",
    },
    icon: {
      marginRight: "0.5em",
    },
    text: {},
  }),
};
