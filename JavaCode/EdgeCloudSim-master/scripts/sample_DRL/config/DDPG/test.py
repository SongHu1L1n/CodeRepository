delay_by_Edge: 1.129
delay_by_Cloud_via_rsu: 0.011
delay_by_Cloud_via_gsm: 0.027
import gym
# import stable_baselines
# from stable_baselines.common.policies import MlpPolicy
# from stable_baselines.common.vec_env import DummyVecEnv
# from stable_baselines import PPO2

env = gym.make('CartPole-v1')
env = DummyVecEnv([lambda: env])  # The algorithms require a vectorized environment to run

model = PPO2(MlpPolicy, env, verbose=1)
model.learn(total_timesteps=10000)

obs = env.reset()
for i in range(1000):
    action, _states = model.predict(obs)
    obs, rewards, dones, info = env.step(action)
    env.render()


def step(action):
    if action == 0:
        reward = target(0) - min(target(1), target(2))
    elif action == 1:
        reward = target(1) - min(target(0), target(2))
    else:
        reward = target(2) - min(target(1), target(0))
    print('采取动作:', action, '得到reward为:', reward)


def target(action):
    speed = [20, 40, 60]
    delays = [1.129, 0.011, 0.027]

    costs = [0.5, 1.5, 1.5]
    storage = [125000, 250000, 250000]

    input_file_size = [20, 40, 20]
    output_file_size = [20, 20, 80]

    return (costs[action] * delays[action] *
            ((input_file_size[0] + output_file_size[0]) * 1024) / storage[action])


if __name__ == "__main__":
    step(1)
